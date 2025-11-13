package com.pathfinder.delivery.application.command.service.impl;

import com.pathfinder.delivery.application.dto.request.CreateDeliveryCommandDto;
import com.pathfinder.delivery.application.dto.request.CreateDeliveryRouteCommandDto;
import com.pathfinder.delivery.application.dto.request.UpdateDeliveryCommandDto;
import com.pathfinder.delivery.application.dto.response.DeliveryDto;
import com.pathfinder.delivery.domain.entity.DeliveryEntity;
import com.pathfinder.delivery.domain.entity.DeliveryRouteEntity;
import com.pathfinder.delivery.domain.enums.DeliveryStatus;
import com.pathfinder.delivery.domain.error.DeliveryErrorCode;
import com.pathfinder.delivery.domain.event.DeliveryEventDto;
import com.pathfinder.delivery.domain.repository.DeliveryRouteRepository;
import com.pathfinder.delivery.domain.repository.DeliveryRepository;
import com.pathfinder.delivery.domain.service.DeliveryStatusValidator;
import com.pathfinder.delivery.domain.service.DeliveryValidator;
import com.pathfinder.delivery.domain.service.DeliveryRouteFactory;
import com.pathfinder.delivery.domain.service.DeliveryManagerAssignmentService;
import com.pathfinder.delivery.domain.value.RouteCalculationResult;
import com.pathfinder.delivery.application.command.service.DeliveryCommandService;
import com.pathfinder.delivery.application.outbox.DeliveryOutboxService;
import com.pathfinder.delivery.infrastructure.external.OrderServiceClient;
import com.pathfinder.delivery.infrastructure.external.client.MessageServiceClient;
import com.pathfinder.delivery.infrastructure.external.dto.MessageRequestDto;
import com.pathfinder.delivery.infrastructure.external.security.filter.JwtAuthorizationFilter.GatewayPrincipal;
import com.pathfinder.global.presentation.exception.PathException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pathfinder.delivery.infrastructure.external.DeliveryManagerServiceClient;
import com.pathfinder.delivery.infrastructure.external.dto.DeliveryManagerDto;
import com.pathfinder.global.presentation.response.ApiResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryCommandServiceImpl implements DeliveryCommandService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryRouteRepository routeRepository;
    private final DeliveryValidator deliveryValidator;
    private final DeliveryRouteFactory routeFactory;
    private final DeliveryOutboxService deliveryOutboxService;
    private final DeliveryStatusValidator statusValidator;
    private final MessageServiceClient messageServiceClient;
    private final DeliveryManagerAssignmentService deliveryManagerAssignmentService;
    private final DeliveryManagerServiceClient deliveryManagerServiceClient;
    private final OrderServiceClient orderServiceClient;

    @Override
    @Transactional
    public DeliveryDto createDelivery(CreateDeliveryCommandDto command) {
        log.info("Creating delivery for orderId: {}", command.getOrderId());

        validateDeliveryCreation(command);
        RouteCalculationResult routeResult = calculateRoute(command);
        DeliveryEntity savedDelivery = createAndSaveDelivery(command, routeResult);
        createAndSaveDeliveryRoutes(savedDelivery, command, routeResult);
        publishDeliveryCreatedEvent(savedDelivery);
        sendSlackNotification(savedDelivery, command);

        orderServiceClient.delivery(command.getOrderId(), savedDelivery.getDeliveryId());

        return savedDelivery.toDeliveryDto();
    }

    private void validateDeliveryCreation(CreateDeliveryCommandDto command) {
        deliveryValidator.validateAndGetOrder(command.getOrderId());
        deliveryValidator.validateAndGetHub(command.getFromHubId());
        if (command.getDeliveryManagerId() != null) {
            deliveryValidator.validateAndGetDeliveryManager(command.getDeliveryManagerId());
        }
    }

    private RouteCalculationResult calculateRoute(CreateDeliveryCommandDto command) {
        return routeFactory.calculateRoute(
            command.getFromHubId(),
            command.getToHubId(),
            command.getExpectedDistance()
        );
    }

    private DeliveryEntity createAndSaveDelivery(CreateDeliveryCommandDto command, RouteCalculationResult routeResult) {
        CreateDeliveryCommandDto commandWithManager = ensureDeliveryManagerAssigned(command);
        DeliveryEntity delivery = commandWithManager.toEntity(routeResult.getTotalExpectedDistance());
        return deliveryRepository.save(delivery);
    }

    private CreateDeliveryCommandDto ensureDeliveryManagerAssigned(CreateDeliveryCommandDto command) {
        if (command.getDeliveryManagerId() != null) {
            return command;
        }
        UUID assignedManagerId = deliveryManagerAssignmentService.assignDeliveryManager(command.getToHubId());
        return command.withDeliveryManagerId(assignedManagerId);
    }

    private void createAndSaveDeliveryRoutes(DeliveryEntity savedDelivery, CreateDeliveryCommandDto command, RouteCalculationResult routeResult) {
        if (routeResult.hasValidPath()) {
            List<DeliveryRouteEntity> routes = routeFactory.createRoutes(
                savedDelivery.getDeliveryId(),
                routeResult.getPath(),
                savedDelivery.getDeliveryManagerId()
            );
            routeRepository.saveAll(routes);
        } else {
            DeliveryRouteEntity initialRoute = routeFactory.createInitialRoute(
                savedDelivery.getDeliveryId(),
                command.getFromHubId(),
                command.getToHubId(),
                routeResult.getTotalExpectedDistance(),
                savedDelivery.getDeliveryManagerId()
            );
            routeRepository.save(initialRoute);
        }
    }

    private void publishDeliveryCreatedEvent(DeliveryEntity delivery) {
        DeliveryEventDto event = delivery.toEvent("CREATED");
        deliveryOutboxService.enqueue(event);
    }

    private void sendSlackNotification(DeliveryEntity delivery, CreateDeliveryCommandDto command) {
        try {
            if (delivery.getFromHubId() != null && delivery.getDeliveryManagerId() != null) {
                ApiResponse<List<DeliveryManagerDto>> response = 
                    deliveryManagerServiceClient.getDeliveryManagersByHubAndType(delivery.getFromHubId(), "HUB");
                List<DeliveryManagerDto> hubManagers = response != null ? response.getData() : null;
                
                if (hubManagers != null && !hubManagers.isEmpty()) {
                    MessageRequestDto messageRequest = MessageRequestDto.builder()
                        .request(buildDeliveryNotificationMessage(delivery, command))
                        .senderId(delivery.getDeliveryManagerId())
                        .receiverId(hubManagers.get(0).getDeliveryManagerId())
                        .build();
                    
                    messageServiceClient.sendSlackMessage(messageRequest);
                    log.info("Slack notification sent for delivery: {} from sender: {} to receiver: {}", 
                        delivery.getDeliveryId(), delivery.getDeliveryManagerId(), hubManagers.get(0).getDeliveryManagerId());
                }
            }
        } catch (Exception e) {
            log.error("Failed to send slack notification for delivery: {}", delivery.getDeliveryId(), e);
        }
    }

    private String buildDeliveryNotificationMessage(DeliveryEntity delivery, CreateDeliveryCommandDto command) {
        return String.format(
            "배송이 생성되었습니다.\n" +
            "배송 ID: %s\n" +
            "주문 ID: %s\n" +
            "출발 허브: %s\n" +
            "목적지 허브: %s\n" +
            "배송지 주소: %s\n" +
            "수령자: %s",
            delivery.getDeliveryId(),
            delivery.getOrderId(),
            delivery.getFromHubId(),
            delivery.getToHubId(),
            command.getDeliveryAddress() != null ? command.getDeliveryAddress() : "미지정",
            command.getReceiverName() != null ? command.getReceiverName() : "미지정"
        );
    }

    @Override
    @Transactional
    @CacheEvict(value = "delivery", key = "#command.deliveryId")
    public DeliveryDto updateDelivery(UpdateDeliveryCommandDto command) {
        log.info("Updating delivery: {}", command.getDeliveryId());

        DeliveryEntity delivery = findDeliveryById(command.getDeliveryId());
        validateDeliveryManagerIfPresent(command);
        DeliveryStatus validatedStatus = validateAndGetNewStatus(command, delivery);
        boolean statusChanged = updateDeliveryEntity(delivery, command, validatedStatus);
        DeliveryEntity savedDelivery = deliveryRepository.save(delivery);
        publishDeliveryUpdatedEvent(savedDelivery, statusChanged);

        return savedDelivery.toDeliveryDto();
    }

    private DeliveryEntity findDeliveryById(UUID deliveryId) {
        return deliveryRepository.findById(deliveryId)
            .orElseThrow(() -> new PathException(DeliveryErrorCode.DELIVERY_NOT_FOUND));
    }

    private void validateDeliveryManagerIfPresent(UpdateDeliveryCommandDto command) {
        if (command.getDeliveryManagerId() != null) {
            deliveryValidator.validateAndGetDeliveryManager(command.getDeliveryManagerId());
        }
    }

    private DeliveryStatus validateAndGetNewStatus(UpdateDeliveryCommandDto command, DeliveryEntity delivery) {
        if (command.getStatus() != null) {
            DeliveryStatus newStatus = DeliveryStatus.valueOf(command.getStatus());
            statusValidator.validateStatusTransition(delivery.getStatus(), newStatus);
            return newStatus;
        }
        return null;
    }

    private boolean updateDeliveryEntity(DeliveryEntity delivery, UpdateDeliveryCommandDto command, DeliveryStatus validatedStatus) {
        return delivery.updateWithCommand(command, validatedStatus);
    }

    private void publishDeliveryUpdatedEvent(DeliveryEntity delivery, boolean statusChanged) {
        String eventType = delivery.determineUpdateEventType(statusChanged);
        DeliveryEventDto event = delivery.toEvent(eventType);
        deliveryOutboxService.enqueue(event);
    }

    @Override
    @Transactional
    @CacheEvict(value = "delivery", key = "#id")
    public void deleteDelivery(UUID id) {
        log.info("Deleting delivery: {}", id);

        DeliveryEntity delivery = findDeliveryById(id);
        cancelAndSoftDeleteDelivery(delivery, id);
        publishDeliveryCancelledEvent(delivery);
    }

    private void cancelAndSoftDeleteDelivery(DeliveryEntity delivery, UUID id) {
        delivery.cancel();
        deliveryRepository.softDelete(id, getCurrentUserId());
    }

    private void publishDeliveryCancelledEvent(DeliveryEntity delivery) {
        DeliveryEventDto event = delivery.toEvent("CANCELLED");
        deliveryOutboxService.enqueue(event);
    }

  
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof GatewayPrincipal principal) {
            return principal.username();
        }
        return "system";
    }
}

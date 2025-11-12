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
import com.pathfinder.delivery.domain.value.RouteCalculationResult;
import com.pathfinder.delivery.application.command.service.DeliveryCommandService;
import com.pathfinder.delivery.application.outbox.DeliveryOutboxService;
import com.pathfinder.delivery.infrastructure.external.security.auth.CustomUserDetails;
import com.pathfinder.global.presentation.exception.PathException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public DeliveryDto createDelivery(CreateDeliveryCommandDto command) {
        log.info("Creating delivery for orderId: {}", command.getOrderId());

        validateDeliveryCreation(command);
        RouteCalculationResult routeResult = calculateRoute(command);
        DeliveryEntity savedDelivery = createAndSaveDelivery(command, routeResult);
        createAndSaveDeliveryRoutes(savedDelivery, command, routeResult);
        publishDeliveryCreatedEvent(savedDelivery);

        return savedDelivery.toDeliveryDto();
    }

    private void validateDeliveryCreation(CreateDeliveryCommandDto command) {
        deliveryValidator.validateAndGetOrder(command.getOrderId());
        deliveryValidator.validateAndGetHub(command.getFromHubId());
        deliveryValidator.validateAndGetDeliveryManager(command.getDeliveryManagerId());
    }

    private RouteCalculationResult calculateRoute(CreateDeliveryCommandDto command) {
        return routeFactory.calculateRoute(
            command.getFromHubId(),
            command.getToHubId(),
            command.getExpectedDistance()
        );
    }

    private DeliveryEntity createAndSaveDelivery(CreateDeliveryCommandDto command, RouteCalculationResult routeResult) {
        DeliveryEntity delivery = command.toEntity(routeResult.getTotalExpectedDistance());
        return deliveryRepository.save(delivery);
    }

    private void createAndSaveDeliveryRoutes(DeliveryEntity savedDelivery, CreateDeliveryCommandDto command, RouteCalculationResult routeResult) {
        if (routeResult.hasValidPath()) {
            List<DeliveryRouteEntity> routes = routeFactory.createRoutes(
                savedDelivery.getDeliveryId(),
                routeResult.getPath(),
                command.getDeliveryManagerId()
            );
            routeRepository.saveAll(routes);
        } else {
            DeliveryRouteEntity initialRoute = routeFactory.createInitialRoute(
                savedDelivery.getDeliveryId(),
                command.getFromHubId(),
                command.getToHubId(),
                routeResult.getTotalExpectedDistance(),
                command.getDeliveryManagerId()
            );
            routeRepository.save(initialRoute);
        }
    }

    private void publishDeliveryCreatedEvent(DeliveryEntity delivery) {
        DeliveryEventDto event = delivery.toEvent("CREATED");
        deliveryOutboxService.enqueue(event);
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
        publishDeliveryUpdatedEvent(delivery, statusChanged);

        return delivery.toDeliveryDto();
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
        deliveryRepository.softDelete(id, getCurrentUserIdAsString());
    }

    private void publishDeliveryCancelledEvent(DeliveryEntity delivery) {
        DeliveryEventDto event = delivery.toEvent("CANCELLED");
        deliveryOutboxService.enqueue(event);
    }

  
    private String getCurrentUserIdAsString() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            // UUID를 Long으로 변환할 수 없으므로 mostSignificantBits 사용
            long userId = Math.abs(userDetails.getId().getMostSignificantBits());
            return String.valueOf(userId);
        }
        return "0";
    }
}

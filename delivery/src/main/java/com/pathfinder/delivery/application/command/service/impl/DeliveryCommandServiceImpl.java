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
        
        deliveryValidator.validateAndGetOrder(command.getOrderId());

        deliveryValidator.validateAndGetHub(command.getFromHubId());

        deliveryValidator.validateAndGetDeliveryManager(command.getDeliveryManagerId());

        RouteCalculationResult routeResult = 
            routeFactory.calculateRoute(command.getFromHubId(), command.getToHubId(), command.getExpectedDistance());

        DeliveryEntity delivery = command.toEntity(routeResult.getTotalExpectedDistance());
        DeliveryEntity savedDelivery = deliveryRepository.save(delivery);

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

        DeliveryEventDto event = savedDelivery.toEvent("CREATED");
        deliveryOutboxService.enqueue(event);

        return savedDelivery.toDeliveryDto();
    }

    @Override
    @Transactional
    @CacheEvict(value = "delivery", key = "#command.deliveryId")
    public DeliveryDto updateDelivery(UpdateDeliveryCommandDto command) {
        log.info("Updating delivery: {}", command.getDeliveryId());
        
        DeliveryEntity delivery = deliveryRepository.findById(command.getDeliveryId())
            .orElseThrow(() -> new PathException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        if (command.getDeliveryManagerId() != null) {
            deliveryValidator.validateAndGetDeliveryManager(command.getDeliveryManagerId());
        }

        DeliveryStatus validatedStatus = null;
        if (command.getStatus() != null) {
            DeliveryStatus newStatus = DeliveryStatus.valueOf(command.getStatus());
                statusValidator.validateStatusTransition(delivery.getStatus(), newStatus);
            validatedStatus = newStatus;
            }

        boolean statusChanged = delivery.updateWithCommand(command, validatedStatus);

        String eventType = delivery.determineUpdateEventType(statusChanged);
        DeliveryEventDto event = delivery.toEvent(eventType);
        deliveryOutboxService.enqueue(event);

        return delivery.toDeliveryDto();
    }

    @Override
    @Transactional
    @CacheEvict(value = "delivery", key = "#id")
    public void deleteDelivery(UUID id) {
        log.info("Deleting delivery: {}", id);
        
        DeliveryEntity delivery = deliveryRepository.findById(id)
            .orElseThrow(() -> new PathException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        delivery.cancel();
        // 현재 인증된 사용자 ID 사용 (시스템 작업의 경우 "0")
        deliveryRepository.softDelete(id, getCurrentUserIdAsString());

        DeliveryEventDto event = delivery.toEvent("CANCELLED");
        deliveryOutboxService.enqueue(event);
    }

    /**
     * 현재 인증된 사용자 ID를 Long 파싱 가능한 String으로 반환
     * UUID를 Long으로 변환할 수 없으므로 UUID의 해시코드를 사용
     * 인증 정보가 없으면 "0" 반환 (시스템 작업)
     */
    private String getCurrentUserIdAsString() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            // UUID를 Long으로 변환할 수 없으므로 mostSignificantBits 사용
            long userId = Math.abs(userDetails.getId().getMostSignificantBits());
            return String.valueOf(userId);
        }
        // 인증 정보가 없거나 익명 사용자인 경우 시스템 ID (0) 사용
        return "0";
    }
}

package com.pathfinder.delivery.application.command.service;

import com.pathfinder.delivery.application.dto.request.CreateDeliveryCommand;
import com.pathfinder.delivery.application.dto.request.CreateDeliveryRouteCommand;
import com.pathfinder.delivery.application.dto.request.UpdateDeliveryCommand;
import com.pathfinder.delivery.application.dto.response.DeliveryDto;
import com.pathfinder.delivery.domain.entity.DeliveryEntity;
import com.pathfinder.delivery.domain.entity.DeliveryRouteEntity;
import com.pathfinder.delivery.domain.enums.DeliveryStatus;
import com.pathfinder.delivery.domain.error.DeliveryErrorCode;
import com.pathfinder.delivery.domain.event.DeliveryDomainEvent;
import com.pathfinder.delivery.domain.repository.DeliveryRouteRepository;
import com.pathfinder.delivery.domain.repository.DeliveryRepository;
import com.pathfinder.delivery.domain.service.DeliveryStatusValidator;
import com.pathfinder.delivery.domain.service.RouteCalculationService;
import com.pathfinder.delivery.infrastructure.external.DeliveryManagerServiceClient;
import com.pathfinder.delivery.infrastructure.external.HubServiceClient;
import com.pathfinder.delivery.infrastructure.external.OrderServiceClient;
import com.pathfinder.delivery.infrastructure.external.dto.DeliveryManagerDto;
import com.pathfinder.delivery.infrastructure.external.dto.HubDto;
import com.pathfinder.delivery.infrastructure.external.dto.HubRouteDto;
import com.pathfinder.delivery.infrastructure.external.dto.OrderDto;
import com.pathfinder.delivery.infrastructure.external.dto.RouteCalculationDto;
import com.pathfinder.delivery.infrastructure.messaging.DeliveryEventPublisher;
import com.pathfinder.global.presentation.exception.PathException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final OrderServiceClient orderServiceClient;
    private final HubServiceClient hubServiceClient;
    private final DeliveryManagerServiceClient deliveryManagerServiceClient;
    private final DeliveryEventPublisher eventPublisher;
    private final RouteCalculationService routeCalculationService;
    private final DeliveryStatusValidator statusValidator;

    @Override
    @Transactional
    public DeliveryDto createDelivery(CreateDeliveryCommand command) {
        log.info("Creating delivery for orderId: {}", command.getOrderId());
        
        // 1. 주문 검증 (Order Service 호출)
        OrderDto order = orderServiceClient.getOrder(command.getOrderId());
        if (order == null) {
            throw new PathException(DeliveryErrorCode.ORDER_NOT_FOUND);
        }

        // 2. 허브 검증 (Hub Service 호출)
        if (command.getFromHubId() != null) {
            HubDto hub = hubServiceClient.getHub(command.getFromHubId());
            if (hub == null) {
                throw new PathException(DeliveryErrorCode.HUB_NOT_FOUND);
            }
        }

        // 3. 배송 담당자 검증 (Delivery Manager Service 호출)
        DeliveryManagerDto manager = 
            deliveryManagerServiceClient.getDeliveryManager(command.getDeliveryManagerId());
        if (manager == null) {
            throw new PathException(DeliveryErrorCode.DELIVERY_MANAGER_NOT_FOUND);
        }

        // 4. 경로 계산 (Hub Service 호출)
        List<UUID> routePath = null;
        BigDecimal totalExpectedDistance = command.getExpectedDistance();
        if (command.getFromHubId() != null && command.getToHubId() != null) {
            RouteCalculationDto routeCalculation = 
                routeCalculationService.calculateShortestPath(command.getFromHubId(), command.getToHubId());
            routePath = routeCalculation.getPath();
            if (totalExpectedDistance == null && routeCalculation.getTotalDistance() != null) {
                totalExpectedDistance = BigDecimal.valueOf(routeCalculation.getTotalDistance());
            }
        }

        // 5. 배송 엔티티 생성
        DeliveryEntity delivery = command.toEntity(totalExpectedDistance);
        DeliveryEntity savedDelivery = deliveryRepository.save(delivery);

        // 6. 배송 경로 기록 생성 (경로가 있는 경우)
        if (routePath != null && routePath.size() > 1) {
            for (int i = 0; i < routePath.size() - 1; i++) {
                UUID fromHub = routePath.get(i);
                UUID toHub = routePath.get(i + 1);
                
                // Hub Service에서 경로 정보 가져오기
                HubRouteDto hubRoute = 
                    hubServiceClient.findRouteByDepartAndArrive(fromHub, toHub);
                
                DeliveryRouteEntity route = CreateDeliveryRouteCommand.createRouteWithHubInfo(
                    savedDelivery.getDeliveryId(),
                    fromHub,
                    toHub,
                    i,
                    hubRoute != null ? hubRoute.getTime() : null,
                    hubRoute != null && hubRoute.getDistance() != null ? 
                        BigDecimal.valueOf(hubRoute.getDistance()) : null,
                    command.getDeliveryManagerId()
                );

                routeRepository.save(route);
            }
        } else {
            // 경로가 없는 경우 초기 경로 기록 생성
            DeliveryRouteEntity initialRoute = CreateDeliveryRouteCommand.createRouteWithHubInfo(
                savedDelivery.getDeliveryId(),
                command.getFromHubId(),
                command.getToHubId(),
                0,
                null,
                totalExpectedDistance,
                command.getDeliveryManagerId()
            );

            routeRepository.save(initialRoute);
        }

        // 7. 도메인 이벤트 발행
        DeliveryDomainEvent event = DeliveryDomainEvent.from(savedDelivery, "CREATED");
        eventPublisher.publishDeliveryEvent(event);

        return savedDelivery.toDeliveryDto();
    }

    @Override
    @Transactional
    public DeliveryDto updateDelivery(UpdateDeliveryCommand command) {
        log.info("Updating delivery: {}", command.getDeliveryId());
        
        DeliveryEntity delivery = deliveryRepository.findById(command.getDeliveryId())
            .orElseThrow(() -> new PathException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        // 상태 전이 검증 (Domain Service 위임)
        if (command.getStatus() != null) {
            DeliveryStatus newStatus = DeliveryStatus.valueOf(command.getStatus());
            if (newStatus != delivery.getStatus()) {
                statusValidator.validateStatusTransition(delivery.getStatus(), newStatus);
                delivery.updateStatus(newStatus);
            }
        }

        // 담당자 변경 검증 (외부 서비스 호출)
        if (command.getDeliveryManagerId() != null) {
            DeliveryManagerDto manager = deliveryManagerServiceClient.getDeliveryManager(command.getDeliveryManagerId());
            if (manager == null) {
                throw new PathException(DeliveryErrorCode.DELIVERY_MANAGER_NOT_FOUND);
            }
        }

        // Entity 업데이트 (Entity 메소드 위임)
        delivery.update(command);

        DeliveryEntity updatedDelivery = deliveryRepository.save(delivery);

        // 도메인 이벤트 발행
        String eventType = (command.getStatus() != null) ? "STATUS_CHANGED" : "UPDATED";
        DeliveryDomainEvent event = DeliveryDomainEvent.from(updatedDelivery, eventType);
        eventPublisher.publishDeliveryEvent(event);

        return updatedDelivery.toDeliveryDto();
    }

    @Override
    @Transactional
    public void deleteDelivery(UUID id) {
        log.info("Deleting delivery: {}", id);
        
        DeliveryEntity delivery = deliveryRepository.findById(id)
            .orElseThrow(() -> new PathException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        // 취소 상태로 변경 (Entity 메소드 위임)
        delivery.cancel();
        deliveryRepository.save(delivery);

        // 소프트 삭제
        deliveryRepository.softDelete(id, "system");

        // 도메인 이벤트 발행
        DeliveryDomainEvent event = DeliveryDomainEvent.from(delivery, "CANCELLED");
        eventPublisher.publishDeliveryEvent(event);
    }
}

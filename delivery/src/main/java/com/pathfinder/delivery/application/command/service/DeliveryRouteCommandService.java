package com.pathfinder.delivery.application.command.service;

import com.pathfinder.delivery.application.dto.request.CreateDeliveryRouteCommand;
import com.pathfinder.delivery.application.dto.response.DeliveryRouteDto;
import com.pathfinder.delivery.domain.entity.DeliveryEntity;
import com.pathfinder.delivery.domain.entity.DeliveryRouteEntity;
import com.pathfinder.delivery.domain.error.DeliveryErrorCode;
import com.pathfinder.delivery.domain.repository.DeliveryRouteRepository;
import com.pathfinder.delivery.domain.repository.DeliveryRepository;
import com.pathfinder.global.presentation.exception.PathException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryRouteCommandService {

    private final DeliveryRouteRepository routeRepository;
    private final DeliveryRepository deliveryRepository;

    @Transactional
    public DeliveryRouteDto createRoute(CreateDeliveryRouteCommand command) {
        log.info("Creating delivery route for deliveryId: {}", command.getDeliveryId());
        
        DeliveryEntity delivery = deliveryRepository.findById(command.getDeliveryId())
            .orElseThrow(() -> new PathException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        // 다음 시퀀스 번호 계산
        List<DeliveryRouteEntity> existingRoutes = routeRepository.findByDeliveryIdOrderBySequence(command.getDeliveryId());
        int nextSequence = existingRoutes.isEmpty() ? 0 : existingRoutes.size();

        // Command를 Entity로 변환 (간소화)
        DeliveryRouteEntity route = command.toEntity(nextSequence);
        DeliveryRouteEntity savedRoute = routeRepository.save(route);
        
        return savedRoute.toDeliveryRouteDto();
    }

    @Transactional
    public DeliveryRouteDto updateRoute(UUID routeId, CreateDeliveryRouteCommand command) {
        log.info("Updating delivery route: {}", routeId);
        
        DeliveryRouteEntity route = routeRepository.findById(routeId)
            .orElseThrow(() -> new PathException(DeliveryErrorCode.ROUTE_NOT_FOUND));

        if (command.getStatus() != null) {
            route.updateStatus(command.getStatus());
        }

        if (command.getActualTime() != null || command.getActualDistance() != null) {
            route.updateActualMetrics(
                command.getActualTime() != null ? command.getActualTime() : route.getActualTime(),
                command.getActualDistance() != null ? command.getActualDistance() : route.getActualDistance()
            );
        }

        if (command.getExpectedTime() != null || command.getExpectedDistance() != null) {
            route.updateExpectedMetrics(
                command.getExpectedTime() != null ? command.getExpectedTime() : route.getExpectedTime(),
                command.getExpectedDistance() != null ? command.getExpectedDistance() : route.getExpectedDistance()
            );
        }

        DeliveryRouteEntity updatedRoute = routeRepository.save(route);
        return updatedRoute.toDeliveryRouteDto();
    }
}


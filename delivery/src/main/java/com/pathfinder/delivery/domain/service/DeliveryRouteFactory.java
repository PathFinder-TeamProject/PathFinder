package com.pathfinder.delivery.domain.service;

import com.pathfinder.delivery.application.dto.request.CreateDeliveryRouteCommandDto;
import com.pathfinder.delivery.domain.entity.DeliveryRouteEntity;
import com.pathfinder.delivery.domain.value.RouteCalculationResult;
import com.pathfinder.delivery.infrastructure.external.HubServiceClient;
import com.pathfinder.delivery.infrastructure.external.dto.HubRouteDto;
import com.pathfinder.delivery.infrastructure.external.dto.RouteCalculationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 배송 경로 생성을 담당하는 Domain Service
 * 경로 계산 결과를 기반으로 DeliveryRoute Entity를 생성합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryRouteFactory {

    private final HubServiceClient hubServiceClient;
    private final RouteCalculationService routeCalculationService;

    /**
     * 경로를 계산하고 총 예상 거리를 반환합니다.
     */
    public RouteCalculationResult calculateRoute(UUID fromHubId, UUID toHubId, BigDecimal expectedDistance) {
        if (fromHubId == null || toHubId == null) {
            return new RouteCalculationResult(null, expectedDistance);
        }

        RouteCalculationDto calculation = routeCalculationService.calculateShortestPath(fromHubId, toHubId);
        List<UUID> path = calculation.getPath();
        
        BigDecimal totalDistance = expectedDistance;
        if (totalDistance == null && calculation.getTotalDistance() != null) {
            totalDistance = BigDecimal.valueOf(calculation.getTotalDistance());
        }

        return new RouteCalculationResult(path, totalDistance);
    }

    /**
     * 계산된 경로를 기반으로 DeliveryRoute Entity 리스트를 생성합니다.
     */
    public List<DeliveryRouteEntity> createRoutes(
            UUID deliveryId,
            List<UUID> routePath,
            UUID deliveryManagerId) {
        
        List<DeliveryRouteEntity> routes = new ArrayList<>();
        
        if (routePath == null || routePath.size() <= 1) {
            return routes;
        }

        for (int i = 0; i < routePath.size() - 1; i++) {
            UUID fromHub = routePath.get(i);
            UUID toHub = routePath.get(i + 1);
            
            HubRouteDto hubRoute = hubServiceClient.findRouteByDepartAndArrive(fromHub, toHub);
            
            DeliveryRouteEntity route = CreateDeliveryRouteCommandDto.createRouteWithHubInfo(
                deliveryId,
                fromHub,
                toHub,
                i,
                hubRoute != null ? hubRoute.getTime() : null,
                hubRoute != null && hubRoute.getDistance() != null ? 
                    BigDecimal.valueOf(hubRoute.getDistance()) : null,
                deliveryManagerId
            );
            
            routes.add(route);
        }

        return routes;
    }

    /**
     * 초기 경로 기록을 생성합니다 (경로가 계산되지 않은 경우).
     */
    public DeliveryRouteEntity createInitialRoute(
            UUID deliveryId,
            UUID fromHubId,
            UUID toHubId,
            BigDecimal expectedDistance,
            UUID deliveryManagerId) {
        
        return CreateDeliveryRouteCommandDto.createRouteWithHubInfo(
            deliveryId,
            fromHubId,
            toHubId,
            0,
            null,
            expectedDistance,
            deliveryManagerId
        );
    }
}


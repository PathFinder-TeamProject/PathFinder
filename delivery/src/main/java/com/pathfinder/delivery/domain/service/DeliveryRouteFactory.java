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

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryRouteFactory {

    private final HubServiceClient hubServiceClient;
    private final RouteCalculationService routeCalculationService;
    private final DeliveryManagerAssignmentService deliveryManagerAssignmentService;

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

    public List<DeliveryRouteEntity> createRoutes(
            UUID deliveryId,
            List<UUID> routePath,
            Long deliveryManagerId) {
        
        List<DeliveryRouteEntity> routes = new ArrayList<>();
        
        if (routePath == null || routePath.size() <= 1) {
            return routes;
        }

        for (int i = 0; i < routePath.size() - 1; i++) {
            UUID fromHub = routePath.get(i);
            UUID toHub = routePath.get(i + 1);
            
            HubRouteDto hubRoute = hubServiceClient.findRouteByDepartAndArrive(fromHub, toHub);
            
            Long hubManagerId = deliveryManagerAssignmentService.assignDeliveryManager(fromHub, "HUB");
            
            DeliveryRouteEntity route = CreateDeliveryRouteCommandDto.createRouteWithHubInfo(
                deliveryId,
                fromHub,
                toHub,
                i,
                hubRoute != null ? hubRoute.getTime() : null,
                hubRoute != null && hubRoute.getDistance() != null ? 
                    BigDecimal.valueOf(hubRoute.getDistance()) : null,
                hubManagerId
            );
            
            routes.add(route);
        }

        return routes;
    }

    public DeliveryRouteEntity createInitialRoute(
            UUID deliveryId,
            UUID fromHubId,
            UUID toHubId,
            BigDecimal expectedDistance,
            Long deliveryManagerId) {
        
        Long hubManagerId = fromHubId != null ? 
            deliveryManagerAssignmentService.assignDeliveryManager(fromHubId, "HUB") : null;
        
        return CreateDeliveryRouteCommandDto.createRouteWithHubInfo(
            deliveryId,
            fromHubId,
            toHubId,
            0,
            null,
            expectedDistance,
            hubManagerId
        );
    }
}


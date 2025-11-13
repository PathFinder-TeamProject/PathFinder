package com.pathfinder.delivery.domain.service;

import com.pathfinder.delivery.infrastructure.external.HubServiceClient;
import com.pathfinder.delivery.infrastructure.external.dto.HubRouteDto;
import com.pathfinder.delivery.infrastructure.external.dto.RouteCalculationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RouteCalculationService {

    private final HubServiceClient hubServiceClient;

    public RouteCalculationDto calculateShortestPath(UUID start, UUID end) {
        log.info("Calculating shortest path via Hub Service: {} -> {}", start, end);
        List<HubRouteDto> routes = hubServiceClient.findPath(start, end);
        if (routes == null || routes.isEmpty()) {
            return RouteCalculationDto.builder()
                .path(List.of(start, end))
                .totalDistance(0.0)
                .build();
        }
        
        List<UUID> path = new java.util.ArrayList<>();
        double totalDistance = 0.0;
        
        path.add(start);
        for (HubRouteDto route : routes) {
            if (!path.contains(route.getArrive())) {
                path.add(route.getArrive());
            }
            if (route.getDistance() != null) {
                totalDistance += route.getDistance();
            }
        }
        if (!path.contains(end)) {
            path.add(end);
        }
        
        return RouteCalculationDto.builder()
            .path(path)
            .totalDistance(totalDistance)
            .build();
    }

    public List<HubRouteDto> getAllRoutes() {
        log.debug("Getting all hub routes from Hub Service");
        return hubServiceClient.getAllHubRoutes();
    }

    public HubRouteDto getRoute(UUID depart, UUID arrive) {
        log.debug("Getting hub route from Hub Service: {} -> {}", depart, arrive);
        List<HubRouteDto> routes = hubServiceClient.findPath(depart, arrive);
        if (routes != null && !routes.isEmpty()) {
            return routes.stream()
                .filter(r -> r.getDepart().equals(depart) && r.getArrive().equals(arrive))
                .findFirst()
                .orElse(routes.get(0));
        }
        return null;
    }
}

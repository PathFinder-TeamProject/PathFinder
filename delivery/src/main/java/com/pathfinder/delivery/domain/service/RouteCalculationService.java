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
        return hubServiceClient.calculateRoute(start, end);
    }

    public List<HubRouteDto> getAllRoutes() {
        log.debug("Getting all hub routes from Hub Service");
        return hubServiceClient.getAllHubRoutes();
    }

    public HubRouteDto getRoute(UUID depart, UUID arrive) {
        log.debug("Getting hub route from Hub Service: {} -> {}", depart, arrive);
        return hubServiceClient.findRouteByDepartAndArrive(depart, arrive);
    }
}

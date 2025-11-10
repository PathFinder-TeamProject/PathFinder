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

    /**
     * Hub Service를 통해 최단 경로 계산 (Hub-to-Hub Relay)
     * Hub Service에서 P2P + Hub-to-Hub Relay 알고리즘을 처리함
     */
    public RouteCalculationDto calculateShortestPath(UUID start, UUID end) {
        log.info("Calculating shortest path via Hub Service: {} -> {}", start, end);
        return hubServiceClient.calculateRoute(start, end);
    }

    /**
     * Hub Service를 통해 경로 정보 조회
     */
    public List<HubRouteDto> getAllRoutes() {
        log.debug("Getting all hub routes from Hub Service");
        return hubServiceClient.getAllHubRoutes();
    }

    /**
     * Hub Service를 통해 특정 경로 조회
     */
    public HubRouteDto getRoute(UUID depart, UUID arrive) {
        log.debug("Getting hub route from Hub Service: {} -> {}", depart, arrive);
        return hubServiceClient.findRouteByDepartAndArrive(depart, arrive);
    }
}

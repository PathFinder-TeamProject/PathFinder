package com.pathfinder.delivery.infrastructure.external.fallback;

import com.pathfinder.delivery.infrastructure.external.HubServiceClient;
import com.pathfinder.delivery.infrastructure.external.dto.HubDto;
import com.pathfinder.delivery.infrastructure.external.dto.HubRouteDto;
import com.pathfinder.delivery.infrastructure.external.dto.RouteCalculationDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@Profile("!local")
public class HubServiceFallback implements HubServiceClient {

    @Override
    public HubDto getHub(UUID hubId) {
        log.error("Hub Service Circuit Breaker activated for hubId: {}", hubId);
        return null;
    }

    @Override
    public List<HubRouteDto> getAllHubRoutes() {
        log.error("Hub Service Circuit Breaker activated for getAllHubRoutes");
        return Collections.emptyList();
    }

    @Override
    public HubRouteDto getHubRoute(UUID routeId) {
        log.error("Hub Service Circuit Breaker activated for routeId: {}", routeId);
        return null;
    }

    @Override
    public List<HubRouteDto> getRoutesByDepart(UUID departHubId) {
        log.error("Hub Service Circuit Breaker activated for departHubId: {}", departHubId);
        return Collections.emptyList();
    }

    @Override
    public List<HubRouteDto> getRoutesByArrive(UUID arriveHubId) {
        log.error("Hub Service Circuit Breaker activated for arriveHubId: {}", arriveHubId);
        return Collections.emptyList();
    }

    @Override
    public HubRouteDto findRouteByDepartAndArrive(UUID depart, UUID arrive) {
        log.error("Hub Service Circuit Breaker activated for depart: {}, arrive: {}", depart, arrive);
        return null;
    }

    @Override
    public RouteCalculationDto calculateRoute(UUID start, UUID end) {
        log.error("Hub Service Circuit Breaker activated for calculateRoute: {} -> {}", start, end);
        return null;
    }
}


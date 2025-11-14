package com.pathfinder.delivery.infrastructure.external.fallback;

import com.pathfinder.delivery.infrastructure.external.client.HubServiceClient;
import com.pathfinder.delivery.infrastructure.external.dto.HubDto;
import com.pathfinder.delivery.infrastructure.external.dto.HubRouteDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
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
    public List<HubRouteDto> findPath(UUID origin, UUID destination) {
        log.error("Hub Service Circuit Breaker activated for findPath: {} -> {}", origin, destination);
        return Collections.emptyList();
    }
}


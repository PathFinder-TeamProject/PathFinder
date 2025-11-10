package com.pathfinder.delivery.application.query.service.impl;

import com.pathfinder.delivery.application.dto.response.RouteCalculationResultDto;
import com.pathfinder.delivery.application.query.service.RouteCalculationQueryService;
import com.pathfinder.delivery.domain.service.RouteCalculationService;
import com.pathfinder.delivery.infrastructure.external.dto.RouteCalculationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteCalculationQueryServiceImpl implements RouteCalculationQueryService {

    private final RouteCalculationService routeCalculationService;

    @Override
    public RouteCalculationResultDto calculateRoute(UUID start, UUID end) {
        log.info("Calculating route: {} -> {}", start, end);

        RouteCalculationDto result = routeCalculationService.calculateShortestPath(start, end);

        return result.toResultDto();
    }
}


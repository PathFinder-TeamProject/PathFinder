package com.pathfinder.delivery.application.query.service;

import com.pathfinder.delivery.application.dto.response.RouteCalculationResultDto;

import java.util.UUID;

public interface RouteCalculationQueryService {

    RouteCalculationResultDto calculateRoute(UUID start, UUID end);
}

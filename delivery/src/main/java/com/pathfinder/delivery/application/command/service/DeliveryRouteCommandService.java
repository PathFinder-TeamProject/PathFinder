package com.pathfinder.delivery.application.command.service;

import com.pathfinder.delivery.application.dto.request.CreateDeliveryRouteCommandDto;
import com.pathfinder.delivery.application.dto.response.DeliveryRouteDto;

import java.util.UUID;

public interface DeliveryRouteCommandService {

    DeliveryRouteDto createRoute(CreateDeliveryRouteCommandDto command);

    DeliveryRouteDto updateRoute(UUID routeId, CreateDeliveryRouteCommandDto command);
}

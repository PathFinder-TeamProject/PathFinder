package com.pathfinder.delivery.application.query.service;

import com.pathfinder.delivery.application.dto.response.DeliveryRouteDto;

import java.util.List;
import java.util.UUID;

public interface DeliveryRouteQueryService {

    DeliveryRouteDto findById(UUID routeId);

    List<DeliveryRouteDto> findByDeliveryId(UUID deliveryId);
}

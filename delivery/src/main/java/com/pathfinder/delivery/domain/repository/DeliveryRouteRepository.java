package com.pathfinder.delivery.domain.repository;

import com.pathfinder.delivery.domain.entity.DeliveryRouteEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryRouteRepository {
    DeliveryRouteEntity save(DeliveryRouteEntity entity);
    Optional<DeliveryRouteEntity> findById(UUID id);
    List<DeliveryRouteEntity> findByDeliveryId(UUID deliveryId);
    List<DeliveryRouteEntity> findByDeliveryIdOrderBySequence(UUID deliveryId);
}


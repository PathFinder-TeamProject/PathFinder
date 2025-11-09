package com.pathfinder.delivery.domain.repository;

import com.pathfinder.delivery.domain.entity.DeliveryEntity;
import com.pathfinder.delivery.domain.entity.DeliveryRouteEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository {
    DeliveryEntity save(DeliveryEntity entity);
    Optional<DeliveryEntity> findById(UUID id);
    Optional<DeliveryEntity> findByOrderId(UUID orderId);
    List<DeliveryEntity> findAll();
    void softDelete(UUID id, String deletedBy);
}

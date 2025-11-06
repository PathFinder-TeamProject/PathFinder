package com.pathfinder.delivery.domain.repository;

import com.pathfinder.delivery.domain.entity.DeliveryEntity;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository {
    DeliveryEntity save(DeliveryEntity entity);
    Optional<DeliveryEntity> findById(Long id);
    List<DeliveryEntity> findAll();
    Optional<DeliveryEntity> findByOrderId(Long orderId);
    void deleteById(Long id);
}


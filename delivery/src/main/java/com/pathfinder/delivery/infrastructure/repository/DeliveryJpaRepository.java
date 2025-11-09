package com.pathfinder.delivery.infrastructure.repository;

import com.pathfinder.delivery.domain.entity.DeliveryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryJpaRepository extends JpaRepository<DeliveryEntity, UUID> {
    Optional<DeliveryEntity> findByOrderIdAndDeletedAtIsNull(UUID orderId);

    Optional<DeliveryEntity> findByDeliveryIdAndDeletedAtIsNull(UUID deliveryId);
}

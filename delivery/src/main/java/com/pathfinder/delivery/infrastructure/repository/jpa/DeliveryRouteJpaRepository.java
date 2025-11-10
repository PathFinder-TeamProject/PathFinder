package com.pathfinder.delivery.infrastructure.repository;

import com.pathfinder.delivery.domain.entity.DeliveryRouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryRouteJpaRepository extends JpaRepository<DeliveryRouteEntity, UUID> {
    Optional<DeliveryRouteEntity> findByRouteIdAndDeletedAtIsNull(UUID routeId);

    List<DeliveryRouteEntity> findByDeliveryIdAndDeletedAtIsNullOrderBySequenceAsc(UUID deliveryId);

    List<DeliveryRouteEntity> findByDeliveryIdAndDeletedAtIsNull(UUID deliveryId);
}

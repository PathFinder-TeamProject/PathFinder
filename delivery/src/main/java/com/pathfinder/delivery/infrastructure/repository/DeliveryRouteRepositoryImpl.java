package com.pathfinder.delivery.infrastructure.repository;

import com.pathfinder.delivery.domain.entity.DeliveryRouteEntity;
import com.pathfinder.delivery.domain.repository.DeliveryRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliveryRouteRepositoryImpl implements DeliveryRouteRepository {

    private final DeliveryRouteJpaRepository jpaRepository;

    @Override
    public DeliveryRouteEntity save(DeliveryRouteEntity entity) {
        return jpaRepository.save(entity);
    }

    @Override
    public Optional<DeliveryRouteEntity> findById(UUID id) {
        return jpaRepository.findByRouteIdAndDeletedAtIsNull(id);
    }

    @Override
    public List<DeliveryRouteEntity> findByDeliveryId(UUID deliveryId) {
        return jpaRepository.findByDeliveryIdAndDeletedAtIsNull(deliveryId);
    }

    @Override
    public List<DeliveryRouteEntity> findByDeliveryIdOrderBySequence(UUID deliveryId) {
        return jpaRepository.findByDeliveryIdAndDeletedAtIsNullOrderBySequenceAsc(deliveryId);
    }
}


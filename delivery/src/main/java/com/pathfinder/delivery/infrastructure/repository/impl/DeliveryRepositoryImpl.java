package com.pathfinder.delivery.infrastructure.repository;

import com.pathfinder.delivery.domain.entity.DeliveryEntity;
import com.pathfinder.delivery.domain.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements DeliveryRepository {

    private final DeliveryJpaRepository jpaRepository;

    @Override
    public DeliveryEntity save(DeliveryEntity entity) {
        return jpaRepository.save(entity);
    }

    @Override
    public Optional<DeliveryEntity> findById(UUID id) {
        return jpaRepository.findByDeliveryIdAndDeletedAtIsNull(id);
    }

    @Override
    public Optional<DeliveryEntity> findByOrderId(UUID orderId) {
        return jpaRepository.findByOrderIdAndDeletedAtIsNull(orderId);
    }

    @Override
    public List<DeliveryEntity> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void softDelete(UUID id, String deletedBy) {
        jpaRepository.findById(id).ifPresent(entity -> {
            entity.softDelete(Instant.now(), deletedBy);
            jpaRepository.save(entity);
        });
    }
}

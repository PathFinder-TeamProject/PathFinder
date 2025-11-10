package com.pathfinder.delivery.infrastructure.repository.impl;

import com.pathfinder.delivery.domain.entity.DeliveryOutboxEntity;
import com.pathfinder.delivery.domain.enums.DeliveryOutboxStatus;
import com.pathfinder.delivery.domain.repository.DeliveryOutboxRepository;
import com.pathfinder.delivery.infrastructure.repository.DeliveryOutboxJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DeliveryOutboxRepositoryImpl implements DeliveryOutboxRepository {

    private final DeliveryOutboxJpaRepository jpaRepository;

    @Override
    public DeliveryOutboxEntity save(DeliveryOutboxEntity entity) {
        return jpaRepository.save(entity);
    }

    @Override
    public List<DeliveryOutboxEntity> findByStatusWithLock(DeliveryOutboxStatus status, int batchSize) {
        return jpaRepository.findByStatusForUpdate(status, PageRequest.of(0, batchSize));
    }

    @Override
    public void saveAll(List<DeliveryOutboxEntity> entities) {
        jpaRepository.saveAll(entities);
    }
}


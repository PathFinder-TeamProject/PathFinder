package com.pathfinder.delivery.domain.repository;

import com.pathfinder.delivery.domain.entity.DeliveryOutboxEntity;
import com.pathfinder.delivery.domain.enums.DeliveryOutboxStatus;

import java.util.List;

public interface DeliveryOutboxRepository {

    DeliveryOutboxEntity save(DeliveryOutboxEntity entity);

    List<DeliveryOutboxEntity> findByStatusWithLock(DeliveryOutboxStatus status, int batchSize);

    void saveAll(List<DeliveryOutboxEntity> entities);
}


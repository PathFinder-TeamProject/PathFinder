package com.pathfinder.delivery_manager.domain.repository;

import com.pathfinder.delivery_manager.domain.entity.DeliveryManagerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryManagerRepository {
    Optional<DeliveryManagerEntity> findByUsername(String username);

    int countByHubId(UUID hubId);

    int maxDeliveryOrderByHubId(UUID hubId);

    Optional<DeliveryManagerEntity> findByDeliveryManagerId(UUID id);

    DeliveryManagerEntity save(DeliveryManagerEntity entity);

    Page<DeliveryManagerEntity> findByHubId(UUID hubId, Pageable pageable);

    List<DeliveryManagerEntity> findByHubId(UUID hubId);

    List<DeliveryManagerEntity> findByHubIdAndType(UUID hubId, String type);

    Page<DeliveryManagerEntity> findAll(Pageable pageable);

    void saveAll(List<DeliveryManagerEntity> managers);
}

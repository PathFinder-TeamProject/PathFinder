package com.pathfinder.delivery_manager.domain.repository;

import com.pathfinder.delivery_manager.domain.entity.DeliveryManagerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DeliveryManagerRepository {
    Optional<DeliveryManagerEntity> findByUsername(String username);

    int countByHubId(Long hubId);

    Optional<DeliveryManagerEntity> findById(Long id);

    DeliveryManagerEntity save(DeliveryManagerEntity entity);

    Page<DeliveryManagerEntity> findByHubId(Long hubId, Pageable pageable);

    List<DeliveryManagerEntity> findByHubId(Long hubId);

    Page<DeliveryManagerEntity> findAll(Pageable pageable);

    void saveAll(List<DeliveryManagerEntity> managers);
}

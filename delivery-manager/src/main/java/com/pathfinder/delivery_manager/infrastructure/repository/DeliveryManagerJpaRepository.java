package com.pathfinder.delivery_manager.infrastructure.repository;

import com.pathfinder.delivery_manager.domain.entity.DeliveryManagerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryManagerJpaRepository extends JpaRepository<DeliveryManagerEntity, Long> {

    Optional<DeliveryManagerEntity> findByUsername(String username);

    int countByHubId(Long hubId);
    Page<DeliveryManagerEntity> findByHubId(Long hubId, Pageable pageable);
}

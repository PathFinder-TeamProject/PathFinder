package com.pathfinder.delivery_manager.infrastructure.repository;

import com.pathfinder.delivery_manager.domain.entity.DeliveryManagerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DeliveryManagerJpaRepository extends JpaRepository<DeliveryManagerEntity, Long> {
    @Query("SELECT d FROM DeliveryManagerEntity d WHERE d.username = :username AND d.deletedAt IS NULL")
    Optional<DeliveryManagerEntity> findByUsername(String username);

    @Query("SELECT COUNT(d) FROM DeliveryManagerEntity d WHERE d.hubId = :hubId AND d.deletedAt IS NULL")
    int countByHubId(Long hubId);

    @Query("SELECT d FROM DeliveryManagerEntity d WHERE d.hubId = :hubId AND d.deletedAt IS NULL")
    Page<DeliveryManagerEntity> findByHubId(Long hubId, Pageable pageable);
}

package com.pathfinder.delivery_manager.infrastructure.repository;

import com.pathfinder.delivery_manager.domain.entity.DeliveryManagerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryManagerJpaRepository extends JpaRepository<DeliveryManagerEntity, UUID> {
    @Query("SELECT d FROM DeliveryManagerEntity d WHERE d.username = :username AND d.deletedAt IS NULL")
    Optional<DeliveryManagerEntity> findByUsername(@Param("username") String username);

    @Query("SELECT COUNT(d) FROM DeliveryManagerEntity d WHERE d.hubId = :hubId AND d.deletedAt IS NULL")
    int countByHubId(UUID hubId);

    @Query("SELECT d FROM DeliveryManagerEntity d WHERE d.hubId = :hubId AND d.deletedAt IS NULL")
    Page<DeliveryManagerEntity> findByHubId(@Param("hubId") UUID hubId, Pageable pageable);

    @Query("SELECT d FROM DeliveryManagerEntity d WHERE d.hubId = :hubId AND d.deletedAt IS NULL")
    List<DeliveryManagerEntity> findByHubId(@Param("hubId") UUID hubId);

    @Query("SELECT d FROM DeliveryManagerEntity d WHERE d.deliveryManagerId = :id AND d.deletedAt IS NULL")
    Optional<DeliveryManagerEntity> findByDeliveryManagerId(UUID id);

    @Query("SELECT COALESCE(MAX(dm.deliveryOrder), 0) FROM DeliveryManagerEntity dm WHERE dm.hubId = :hubId")
    int findMaxDeliverySeqByHubId(@Param("hubId") UUID hubId);
}

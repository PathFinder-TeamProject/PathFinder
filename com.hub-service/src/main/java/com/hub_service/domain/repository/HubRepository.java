package com.hub_service.domain.repository;

import com.hub_service.domain.model.Hub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface HubRepository extends JpaRepository<Hub, UUID>, HubRepositoryCustom {

    @Query("SELECT h FROM Hub h WHERE h.hubId = :hubId AND h.deletedAt IS NULL")
    Optional<Hub> findByHubIdAndDeletedAtIsNull(@Param("hubId") UUID hubId);

    @Query("SELECT h FROM Hub h WHERE h.deletedAt IS NULL")
    List<Hub> findAllByDeletedAtIsNull();
}

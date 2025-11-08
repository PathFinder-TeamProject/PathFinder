package com.hub_service.domain.repository;

import com.hub_service.domain.model.Hub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface HubRepository extends JpaRepository<Hub, UUID> {
    Optional<Hub> findById(UUID id);
}

package com.hub_service.domain.repository;

import com.hub_service.domain.model.Hub;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HubRepository extends JpaRepository<Hub, UUID> {
}

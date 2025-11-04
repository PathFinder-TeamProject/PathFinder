package com.pathfinder.delivery_manager.infrastructure.repository;

import com.pathfinder.delivery_manager.domain.entity.DeliveryManagerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryManagerJpaRepository extends JpaRepository<DeliveryManagerEntity, Long> {

}

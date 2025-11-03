package com.sparta.pathfinder.delivery.infrastructure.repository;

import com.sparta.pathfinder.delivery.domain.entity.DeliveryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryJpaRepository extends JpaRepository<DeliveryEntity, Long> {

}

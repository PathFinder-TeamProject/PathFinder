package com.pathfinder.delivery-managers.infrastructure.repository;

import com.sparta.pathfinder.user.domain.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryManagerJpaRepository extends JpaRepository<DeliveryManagerEntity, Long> {

}

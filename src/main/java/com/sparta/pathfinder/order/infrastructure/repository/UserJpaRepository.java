package com.sparta.pathfinder.order.infrastructure.repository;

import com.sparta.pathfinder.order.domain.entity.OrderEntity;
import com.sparta.pathfinder.user.domain.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<OrderEntity, Long> {

}

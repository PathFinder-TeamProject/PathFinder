package com.sparta.pathfinder.ai.infrastructure.repository;

import com.sparta.pathfinder.ai.domain.entity.AiEntity;
import com.sparta.pathfinder.user.domain.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiJpaRepository extends JpaRepository<AiEntity, Long> {

}

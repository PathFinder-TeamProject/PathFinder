package com.sparta.pathfinder.message.infrastructure.repository;

import com.sparta.pathfinder.message.domain.entity.MessageEntity;
import com.sparta.pathfinder.user.domain.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageJpaRepository extends JpaRepository<MessageEntity, Long> {

}

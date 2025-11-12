package com.pathfinder.message.infrastructure.repository;

import com.pathfinder.message.domain.entity.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MessageJpaRepository extends JpaRepository<MessageEntity, UUID> {

    Page<MessageEntity> findByReceiverId(UUID receiverId, Pageable pageable);

    Page<MessageEntity> findBySenderId(UUID senderId, Pageable pageable);
}

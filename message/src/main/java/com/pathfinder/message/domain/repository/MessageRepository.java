package com.pathfinder.message.domain.repository;

import com.pathfinder.message.domain.entity.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {
    MessageEntity save(MessageEntity messageEntity);
    Optional<MessageEntity> findById(UUID id);
    Page<MessageEntity> findAll(Pageable pageable);
    Page<MessageEntity> findBySenderId(UUID senderId, Pageable pageable);
    Page<MessageEntity> findByReceiverId(UUID receiverId, Pageable pageable);

}

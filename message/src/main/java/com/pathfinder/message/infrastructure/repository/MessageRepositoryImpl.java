package com.pathfinder.message.infrastructure.repository;

import com.pathfinder.message.domain.entity.MessageEntity;
import com.pathfinder.message.domain.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class MessageRepositoryImpl implements MessageRepository {

    private final MessageJpaRepository messageJpaRepository;

    @Override
    public MessageEntity save(MessageEntity messageEntity) {
        return messageJpaRepository.save(messageEntity);
    }

    @Override
    public Optional<MessageEntity> findById(UUID id) {
        return messageJpaRepository.findById(id);
    }

    @Override
    public Page<MessageEntity> findAll(Pageable pageable) {
        return messageJpaRepository.findAll(pageable);
    }

    @Override
    public Page<MessageEntity> findBySenderId(UUID senderId, Pageable pageable) {
        return messageJpaRepository.findBySenderId(senderId, pageable);
    }

    @Override
    public Page<MessageEntity> findByReceiverId(UUID receiverId, Pageable pageable) {
        return messageJpaRepository.findByReceiverId(receiverId, pageable);
    }
}

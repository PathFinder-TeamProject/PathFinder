package com.pathfinder.message.application;


import com.pathfinder.message.domain.entity.MessageEntity;
import com.pathfinder.message.domain.enums.MessageStatus;
import com.pathfinder.message.domain.repository.MessageRepository;
import com.pathfinder.message.infrastructure.global.security.jwt.JwtUserContext;
import com.pathfinder.message.presentation.dto.request.MessageCreateDto;
import com.pathfinder.message.presentation.dto.request.MessageUpdateDto;
import com.pathfinder.message.presentation.dto.response.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    /** 메시지 생성 */
    public MessageResponseDto createMessage(MessageCreateDto dto) {
        MessageEntity message = MessageEntity.builder()
                .request(dto.getRequest())
                .senderId(dto.getSenderId())
                .receiverId(dto.getReceiverId())
                .sendAt(Instant.now())
                .scheduledTime(Instant.now()) // 기본값 설정 가능
                .status(MessageStatus.scheduled) // 초기 상태
                .build();

        return new MessageResponseDto().fromEntity(messageRepository.save(message));
    }

    /** 메시지 단건 조회 */
    public MessageResponseDto getMessage(UUID messageId) {
        MessageEntity entity = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다. id=" + messageId));
        return new MessageResponseDto().fromEntity(entity);
    }

    /** 메시지 전체 조회 (페이징) */
    public Page<MessageResponseDto> getAllMessages(Pageable pageable) {
        return messageRepository.findAll(pageable).map(new MessageResponseDto()::fromEntity);
    }

    /** 메시지 수정 */
    public MessageResponseDto updateMessage(UUID messageId, MessageUpdateDto dto) {
        MessageEntity entity = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다. id=" + messageId));

        // 업데이트
        if (dto.getRequest() != null) entity = MessageEntity.builder()
                .messageId(entity.getMessageId())
                .request(dto.getRequest())
                .senderId(entity.getSenderId())
                .receiverId(entity.getReceiverId())
                .sendAt(entity.getSendAt())
                .scheduledTime(dto.getScheduledTime() != null ? dto.getScheduledTime() : entity.getScheduledTime())
                .status(entity.getStatus())
                .build();

        return new MessageResponseDto().fromEntity(messageRepository.save(entity));
    }

    /** 메시지 삭제 */
    public void deleteMessage(UUID messageId) {
        MessageEntity entity = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다. id=" + messageId));

        entity.delete();
        entity.softDelete(Instant.now(), JwtUserContext.getUsernameFromHeader());
    }
}


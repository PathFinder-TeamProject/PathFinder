package com.pathfinder.message.presentation.dto.response;

import com.pathfinder.message.domain.entity.MessageEntity;
import com.pathfinder.message.domain.enums.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponseDto {

    private UUID messageId;
    private String request;
    private UUID senderId;
    private UUID receiverId;
    private Instant sendAt;
    private Instant scheduledTime;
    private MessageStatus status;

    public MessageResponseDto fromEntity(MessageEntity entity) {
        return MessageResponseDto.builder()
                .messageId(entity.getMessageId())
                .request(entity.getRequest())
                .senderId(entity.getSenderId())
                .receiverId(entity.getReceiverId())
                .sendAt(entity.getSendAt())
                .scheduledTime(entity.getScheduledTime())
                .status(entity.getStatus())
                .build();
    }
}

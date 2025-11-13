package com.pathfinder.delivery.infrastructure.external.dto;

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
    private String status; // MessageStatus enum을 String으로 받음
}


package com.pathfinder.message.presentation.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class MessageCreateDto {
    private String request;

    private UUID senderId;

    private UUID receiverId;
}

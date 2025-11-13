package com.pathfinder.delivery.infrastructure.external.fallback;

import com.pathfinder.delivery.infrastructure.external.client.MessageServiceClient;
import com.pathfinder.delivery.infrastructure.external.dto.MessageRequestDto;
import com.pathfinder.delivery.infrastructure.external.dto.MessageResponseDto;
import com.pathfinder.global.presentation.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageServiceFallback implements MessageServiceClient {
    
    @Override
    public ResponseEntity<ApiResponse<MessageResponseDto>> sendSlackMessage(MessageRequestDto request) {
        log.warn("Message service is unavailable. Failed to send slack message to receiverId: {}", 
            request != null ? request.getReceiverId() : "unknown");
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}


package com.pathfinder.delivery.infrastructure.external.fallback;

import com.pathfinder.delivery.infrastructure.external.client.MessageServiceClient;
import com.pathfinder.delivery.infrastructure.external.dto.MessageRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageServiceFallback implements MessageServiceClient {
    
    @Override
    public void sendSlackMessage(MessageRequestDto request) {
        log.warn("Message service is unavailable. Failed to send slack message to receiverId: {}", 
            request != null ? request.getReceiverId() : "unknown");
    }
}


package com.pathfinder.delivery.infrastructure.external.client;

import com.pathfinder.delivery.infrastructure.external.dto.MessageRequestDto;
import com.pathfinder.delivery.infrastructure.external.fallback.MessageServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "message-service",
    path = "/internal/messages",
    fallback = MessageServiceFallback.class
)
public interface MessageServiceClient {
    
    @PostMapping
    void sendSlackMessage(@RequestBody MessageRequestDto request);
}


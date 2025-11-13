package com.pathfinder.delivery.infrastructure.external.client;

import com.pathfinder.delivery.infrastructure.external.dto.MessageRequestDto;
import com.pathfinder.delivery.infrastructure.external.dto.MessageResponseDto;
import com.pathfinder.delivery.infrastructure.external.fallback.MessageServiceFallback;
import com.pathfinder.global.presentation.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "message-service",
    path = "/api/v1/slack-messages",
    fallback = MessageServiceFallback.class
)
public interface MessageServiceClient {
    
    @PostMapping
    ResponseEntity<ApiResponse<MessageResponseDto>> sendSlackMessage(@RequestBody MessageRequestDto request);
}


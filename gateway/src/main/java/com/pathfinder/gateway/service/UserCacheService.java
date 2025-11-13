package com.pathfinder.gateway.service;

import com.pathfinder.gateway.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class UserCacheService {
    private final WebClient webClient;

    public UserCacheService(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://USER-SERVICE").build();
    }

    public Mono<UserResponseDto> getUser(String username) {
        return webClient.get()
                .uri("/api//users/{username}", username)
                .retrieve()
                .bodyToMono(UserResponseDto.class);
    }
}
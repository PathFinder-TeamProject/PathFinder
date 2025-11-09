package com.pathfinder.gateway.service;

import com.pathfinder.gateway.client.UserFeignClient;
import com.pathfinder.gateway.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserCacheService {

    private final UserFeignClient userFeignClient;
    private final RedisTemplate<String, UserResponseDto> redisTemplate;

    private static final String PREFIX = "USER_INFO:";

    public UserResponseDto getUser(String username) {
        String key = PREFIX + username;
        UserResponseDto cached = redisTemplate.opsForValue().get(key);
        if (cached != null) return cached;

        UserResponseDto fetched = userFeignClient.getUserByUsername(username);
        redisTemplate.opsForValue().set(key, fetched, Duration.ofMinutes(10));
        return fetched;
    }
}
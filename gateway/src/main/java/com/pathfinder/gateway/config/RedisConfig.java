package com.pathfinder.gateway.config;

import com.pathfinder.gateway.dto.UserResponseDto;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisTemplate<String, UserResponseDto> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, UserResponseDto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
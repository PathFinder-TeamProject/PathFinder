package com.pathfinder.delivery.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    
    private ObjectMapper createRedisObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
            .allowIfBaseType(Object.class)
            .build();
        objectMapper.activateDefaultTyping(
            typeValidator, 
            ObjectMapper.DefaultTyping.NON_FINAL,
            com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY
        );
        
        return objectMapper;
    }

    @Bean
    public GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer(createRedisObjectMapper());
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        GenericJackson2JsonRedisSerializer serializer = jackson2JsonRedisSerializer();
        
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
            .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .withCacheConfiguration("delivery", config.entryTtl(Duration.ofHours(2)))
            .withCacheConfiguration("deliveryRouteById", config.entryTtl(Duration.ofHours(2)))
            .withCacheConfiguration("deliveryRoutesByDeliveryId", config.entryTtl(Duration.ofHours(2)))
            .withCacheConfiguration("deliveryEvents", config.entryTtl(Duration.ofHours(1)))
            .withCacheConfiguration("hubRoutes", config.entryTtl(Duration.ofDays(1)))
            .build();
    }
}


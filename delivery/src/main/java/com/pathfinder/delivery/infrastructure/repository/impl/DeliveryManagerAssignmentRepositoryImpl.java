package com.pathfinder.delivery.infrastructure.repository.impl;

import com.pathfinder.delivery.domain.repository.DeliveryManagerAssignmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DeliveryManagerAssignmentRepositoryImpl implements DeliveryManagerAssignmentRepository {

    private static final String KEY_PREFIX = "delivery:manager:lastOrder:";
    private static final long TTL_HOURS = 24 * 7;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Integer getLastAssignedOrder(UUID hubId) {
        String key = KEY_PREFIX + hubId;
        Object value = redisTemplate.opsForValue().get(key);
        
        if (value == null) {
            log.debug("No last assigned order found for hubId: {}", hubId);
            return null;
        }
        
        if (value instanceof Integer) {
            return (Integer) value;
        }
        
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        
        log.warn("Unexpected value type for hubId: {}, value: {}", hubId, value);
        return null;
    }

    @Override
    public void saveLastAssignedOrder(UUID hubId, Integer order) {
        String key = KEY_PREFIX + hubId;
        redisTemplate.opsForValue().set(key, order, TTL_HOURS, TimeUnit.HOURS);
        log.debug("Saved last assigned order for hubId: {}, order: {}", hubId, order);
    }
}


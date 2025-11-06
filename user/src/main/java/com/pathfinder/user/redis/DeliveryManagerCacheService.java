package com.pathfinder.user.redis;

import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.HashOperations;
//import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryManagerCacheService {

  /*  private final RedisTemplate<String, Object> redisTemplate;

    public void put(String username, UUID hubId, String type) {
        HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();
        Map<String, Object> data = new HashMap<>();
        data.put("hubId", hubId.toString());
        data.put("type", type);
        hashOps.putAll("deliveryManager:" + username, data);
    }

    public Map<Object, Object> get(String username) {
        HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();
        return hashOps.entries("deliveryManager:" + username);
    }

    public void remove(String username) {
        redisTemplate.delete("deliveryManager:" + username);
    }*/
}

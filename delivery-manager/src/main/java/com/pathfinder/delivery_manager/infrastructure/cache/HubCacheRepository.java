package com.pathfinder.delivery_manager.infrastructure.cache;

import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class HubCacheRepository {
    private final Set<Long> hubCache = ConcurrentHashMap.newKeySet();

    public void add(Long hubId) { hubCache.add(hubId); }
    public boolean exists(Long hubId) { return hubCache.contains(hubId); }
}
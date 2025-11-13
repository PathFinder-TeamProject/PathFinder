package com.pathfinder.product.infrastructure.global.client;

import com.pathfinder.product.infrastructure.global.dto.HubDto;
import com.pathfinder.product.infrastructure.global.fallback.HubClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "hub-service",
        path = "/api/v1/hubs",
        fallback = HubClientFallback.class
)
public interface HubClient {
    @GetMapping("/{hubId}")
    ResponseEntity<HubDto> getHub(@PathVariable UUID hubId);

}

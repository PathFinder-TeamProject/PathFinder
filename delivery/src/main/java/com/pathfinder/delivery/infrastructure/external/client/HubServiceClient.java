package com.pathfinder.delivery.infrastructure.external.client;

import com.pathfinder.delivery.infrastructure.external.dto.HubDto;
import com.pathfinder.delivery.infrastructure.external.dto.HubRouteDto;
import com.pathfinder.delivery.infrastructure.external.fallback.HubServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(
    name = "hub-service", 
    path = "/api/v1",
    fallback = HubServiceFallback.class
)
public interface HubServiceClient {
    
    @GetMapping("/hubs/{hubId}")
    HubDto getHub(@PathVariable("hubId") UUID hubId);
    
    @GetMapping("/hub-routes")
    List<HubRouteDto> getAllHubRoutes();
    
    @GetMapping("/hub-routes/path")
    List<HubRouteDto> findPath(
        @RequestParam("origin") UUID origin,
        @RequestParam("destination") UUID destination
    );
}

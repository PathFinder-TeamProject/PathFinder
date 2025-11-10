package com.pathfinder.delivery.infrastructure.external;

import com.pathfinder.delivery.infrastructure.external.dto.HubDto;
import com.pathfinder.delivery.infrastructure.external.dto.HubRouteDto;
import com.pathfinder.delivery.infrastructure.external.dto.RouteCalculationDto;
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
    
    @GetMapping("/hub-routes/{routeId}")
    HubRouteDto getHubRoute(@PathVariable("routeId") UUID routeId);
    
    @GetMapping("/hub-routes/depart/{departHubId}")
    List<HubRouteDto> getRoutesByDepart(@PathVariable("departHubId") UUID departHubId);
    
    @GetMapping("/hub-routes/arrive/{arriveHubId}")
    List<HubRouteDto> getRoutesByArrive(@PathVariable("arriveHubId") UUID arriveHubId);
    
    @GetMapping("/hub-routes/find")
    HubRouteDto findRouteByDepartAndArrive(
        @RequestParam("depart") UUID depart,
        @RequestParam("arrive") UUID arrive
    );
    
    @GetMapping("/hub-routes/calculate")
    RouteCalculationDto calculateRoute(
        @RequestParam("start") UUID start,
        @RequestParam("end") UUID end
    );
}

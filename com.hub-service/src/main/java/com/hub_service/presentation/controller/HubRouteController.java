package com.hub_service.presentation.controller;

import com.hub_service.application.HubRouteService;
import com.hub_service.domain.model.HubRoute;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HubRouteController {

    private final HubRouteService hubRouteService;

    @GetMapping("/hub-routes")
    public ResponseEntity<List<HubRoute>> getAllRoutes() {
        return ResponseEntity.ok(hubRouteService.findAllActiveRoutes());
    }

    @PostMapping("/hub-routes")
    public ResponseEntity<HubRoute> createHubRoute(@RequestBody HubRoute hubRoute) {
        return ResponseEntity.ok(hubRouteService.createRoute(hubRoute));
    }

    @DeleteMapping("/hub-routes/{routeId}")
    public ResponseEntity<Void> deleteRoute(@PathVariable UUID routeId, @RequestHeader("X-User") String user) {
        hubRouteService.deleteRouteLogical(routeId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/hub-routes/path")
    public ResponseEntity<List<HubRoute>> findPath(
            @RequestParam UUID origin,
            @RequestParam UUID destination
    ) {
        return ResponseEntity.ok(hubRouteService.findPath(origin, destination));
    }

}

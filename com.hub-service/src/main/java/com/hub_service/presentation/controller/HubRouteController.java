package com.hub_service.presentation.controller;

import com.hub_service.application.HubRouteService;
import com.hub_service.domain.model.HubRoute;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "허브 경로 API", description = "허브 간 경로 관리 및 탐색 기능 제공")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HubRouteController {

    private final HubRouteService hubRouteService;

    @Operation(summary = "허브 경로 조회", description = "등록된 허브 간 경로 목록을 조회합니다.")
    @GetMapping("/hub-routes")
    public ResponseEntity<List<HubRoute>> getAllRoutes() {
        return ResponseEntity.ok(hubRouteService.findAllActiveRoutes());
    }

    @Operation(summary = "허브 경로 생성", description = "출발지, 도착지, 거리, 소요시간 정보를 기반으로 허브 간 경로를 등록합니다.")
    @PostMapping("/hub-routes")
    public ResponseEntity<HubRoute> createHubRoute(@RequestBody HubRoute hubRoute) {
        return ResponseEntity.ok(hubRouteService.createRoute(hubRoute));
    }

    @DeleteMapping("/hub-routes/{routeId}")
    public ResponseEntity<Void> deleteRoute(@PathVariable UUID routeId, @RequestHeader("X-User") String user) {
        hubRouteService.deleteRouteLogical(routeId, user);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "허브 간 최적 경로 탐색", description = "출발 허브와 도착 허브를 기반으로 최단 경로를 계산하여 반환합니다.")
    @GetMapping("/hub-routes/path")
    public ResponseEntity<List<HubRoute>> findPath(
            @RequestParam UUID origin,
            @RequestParam UUID destination
    ) {
        return ResponseEntity.ok(hubRouteService.findPath(origin, destination));
    }

}

package com.pathfinder.delivery.presentation.controller;

import com.pathfinder.delivery.application.command.service.DeliveryRouteCommandService;
import com.pathfinder.delivery.application.dto.request.CreateDeliveryRouteCommand;
import com.pathfinder.delivery.application.dto.response.DeliveryRouteDto;
import com.pathfinder.delivery.application.query.service.DeliveryRouteQueryService;
import com.pathfinder.global.presentation.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deliveries/{deliveryId}/routes")
@RequiredArgsConstructor
public class DeliveryRouteControllerV1 {

    private final DeliveryRouteCommandService commandService;
    private final DeliveryRouteQueryService queryService;

    @PostMapping
    public ResponseEntity<ApiResponse<DeliveryRouteDto>> createRoute(
        @PathVariable UUID deliveryId,
        @Valid @RequestBody CreateDeliveryRouteCommand command
    ) {
        command.setDeliveryId(deliveryId);
        DeliveryRouteDto dto = commandService.createRoute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(dto));
    }

    @PutMapping("/{routeId}")
    public ResponseEntity<ApiResponse<DeliveryRouteDto>> updateRoute(
        @PathVariable UUID deliveryId,
        @PathVariable UUID routeId,
        @Valid @RequestBody CreateDeliveryRouteCommand command
    ) {
        DeliveryRouteDto dto = commandService.updateRoute(routeId, command);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DeliveryRouteDto>>> getRoutes(@PathVariable UUID deliveryId) {
        List<DeliveryRouteDto> routes = queryService.findByDeliveryId(deliveryId);
        return ResponseEntity.ok(ApiResponse.success(routes));
    }

    @GetMapping("/{routeId}")
    public ResponseEntity<ApiResponse<DeliveryRouteDto>> getRoute(
        @PathVariable UUID deliveryId,
        @PathVariable UUID routeId
    ) {
        DeliveryRouteDto dto = queryService.findById(routeId);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }
}

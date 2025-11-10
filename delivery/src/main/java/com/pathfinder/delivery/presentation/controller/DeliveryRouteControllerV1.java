package com.pathfinder.delivery.presentation.controller;

import com.pathfinder.delivery.application.command.service.DeliveryRouteCommandService;
import com.pathfinder.delivery.application.query.service.DeliveryRouteQueryService;
import com.pathfinder.delivery.application.dto.request.CreateDeliveryRouteCommandDto;
import com.pathfinder.delivery.presentation.dto.request.DeliveryRouteRequestDto;
import com.pathfinder.delivery.presentation.dto.response.DeliveryRouteResponseDto;
import com.pathfinder.global.presentation.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deliveries/{deliveryId}/routes")
@RequiredArgsConstructor
public class DeliveryRouteControllerV1 {

    private final DeliveryRouteCommandService commandService;
    private final DeliveryRouteQueryService queryService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MASTER','HUB_MANAGER')")
    public ResponseEntity<ApiResponse<DeliveryRouteResponseDto>> createRoute(
        @PathVariable UUID deliveryId,
        @Valid @RequestBody DeliveryRouteRequestDto request
    ) {
        CreateDeliveryRouteCommandDto command = request.toCommand(deliveryId);
        DeliveryRouteResponseDto dto = commandService.createRoute(command).toResponseDto();
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(dto));
    }

    @PutMapping("/{routeId}")
    @PreAuthorize("hasAnyRole('MASTER','HUB_MANAGER','DELIVERY_MANAGER')")
    public ResponseEntity<ApiResponse<DeliveryRouteResponseDto>> updateRoute(
        @PathVariable UUID deliveryId,
        @PathVariable UUID routeId,
        @Valid @RequestBody DeliveryRouteRequestDto request
    ) {
        DeliveryRouteResponseDto dto = commandService.updateRoute(routeId, request.toCommand(deliveryId))
            .toResponseDto();
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MASTER','HUB_MANAGER','DELIVERY_MANAGER','COMPANY_MANAGER')")
    public ResponseEntity<ApiResponse<List<DeliveryRouteResponseDto>>> getRoutes(@PathVariable UUID deliveryId) {
        List<DeliveryRouteResponseDto> routes = queryService.findByDeliveryId(deliveryId).stream()
            .map(dto -> dto.toResponseDto())
            .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(routes));
    }

    @GetMapping("/{routeId}")
    @PreAuthorize("hasAnyRole('MASTER','HUB_MANAGER','DELIVERY_MANAGER','COMPANY_MANAGER')")
    public ResponseEntity<ApiResponse<DeliveryRouteResponseDto>> getRoute(
        @PathVariable UUID deliveryId,
        @PathVariable UUID routeId
    ) {
        DeliveryRouteResponseDto dto = queryService.findById(routeId).toResponseDto();
        return ResponseEntity.ok(ApiResponse.success(dto));
    }
}

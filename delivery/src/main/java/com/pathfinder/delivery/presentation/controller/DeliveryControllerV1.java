package com.pathfinder.delivery.presentation.controller;

import com.pathfinder.delivery.application.command.service.DeliveryCommandService;
import com.pathfinder.delivery.application.dto.response.DeliveryDto;
import com.pathfinder.delivery.application.query.service.DeliveryQueryService;
import com.pathfinder.delivery.presentation.dto.request.CreateDeliveryRequestDto;
import com.pathfinder.delivery.presentation.dto.request.UpdateDeliveryRequestDto;
import com.pathfinder.delivery.presentation.dto.response.DeliveryResponseDto;
import com.pathfinder.global.presentation.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.pathfinder.delivery.domain.enums.DeliveryStatus;
import java.util.UUID;

@RestController
@RequestMapping("/v1/deliveries")
@RequiredArgsConstructor
public class DeliveryControllerV1 {

    private final DeliveryCommandService commandService;
    private final DeliveryQueryService queryService;

    @PostMapping
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<ApiResponse<DeliveryResponseDto>> createDelivery(@Valid @RequestBody CreateDeliveryRequestDto request) {
        DeliveryDto dto = commandService.createDelivery(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(dto.toResponseDto()));
    }

    @PutMapping("/{deliveryId}")
    @PreAuthorize("hasAnyRole('MASTER','HUB_MANAGER','DELIVERY_MANAGER')")
    public ResponseEntity<ApiResponse<DeliveryResponseDto>> updateDelivery(
        @PathVariable UUID deliveryId,
        @Valid @RequestBody UpdateDeliveryRequestDto request
    ) {
        DeliveryDto dto = commandService.updateDelivery(request.toCommand(deliveryId));
        return ResponseEntity.ok(ApiResponse.success(dto.toResponseDto()));
    }

    @DeleteMapping("/{deliveryId}")
    @PreAuthorize("hasAnyRole('MASTER','HUB_MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteDelivery(@PathVariable UUID deliveryId) {
        commandService.deleteDelivery(deliveryId);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @GetMapping("/{deliveryId}")
    @PreAuthorize("hasAnyRole('MASTER','HUB_MANAGER','DELIVERY_MANAGER','COMPANY_MANAGER')")
    public ResponseEntity<ApiResponse<DeliveryResponseDto>> getDelivery(@PathVariable UUID deliveryId) {
        DeliveryDto dto = queryService.findById(deliveryId);
        return ResponseEntity.ok(ApiResponse.success(dto.toResponseDto()));
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('MASTER','HUB_MANAGER','DELIVERY_MANAGER','COMPANY_MANAGER')")
    public ResponseEntity<ApiResponse<DeliveryResponseDto>> getDeliveryByOrderId(@PathVariable UUID orderId) {
        DeliveryDto dto = queryService.findByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.success(dto.toResponseDto()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MASTER','HUB_MANAGER','DELIVERY_MANAGER','COMPANY_MANAGER')")
    public ResponseEntity<ApiResponse<Page<DeliveryResponseDto>>> searchDeliveries(
        @RequestParam(required = false) UUID hubId,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) Long deliveryManagerId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "DESC") String sortDir
    ) {
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DeliveryDto> dtoPage = queryService.searchDeliveries(
            hubId,
            status != null ? DeliveryStatus.valueOf(status) : null,
            deliveryManagerId,
            pageable
        );

        Page<DeliveryResponseDto> responsePage = dtoPage.map(DeliveryDto::toResponseDto);
        return ResponseEntity.ok(ApiResponse.success(responsePage));
    }
}

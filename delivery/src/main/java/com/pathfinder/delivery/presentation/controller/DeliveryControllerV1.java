package com.pathfinder.delivery.presentation.controller;

import com.pathfinder.delivery.application.command.service.DeliveryCommandService;
import com.pathfinder.delivery.application.dto.response.DeliveryDto;
import com.pathfinder.delivery.application.query.service.DeliveryQueryService;
import com.pathfinder.delivery.presentation.dto.request.CreateDeliveryRequest;
import com.pathfinder.delivery.presentation.dto.request.UpdateDeliveryRequest;
import com.pathfinder.delivery.presentation.dto.response.DeliveryResponse;
import com.pathfinder.global.presentation.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deliveries")
@RequiredArgsConstructor
public class DeliveryControllerV1 {

    private final DeliveryCommandService commandService;
    private final DeliveryQueryService queryService;

    @PostMapping
    public ResponseEntity<ApiResponse<DeliveryResponse>> createDelivery(@Valid @RequestBody CreateDeliveryRequest request) {
        DeliveryDto dto = commandService.createDelivery(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(DeliveryResponse.fromDeliveryDto(dto)));
    }

    @PutMapping("/{deliveryId}")
    public ResponseEntity<ApiResponse<DeliveryResponse>> updateDelivery(
        @PathVariable UUID deliveryId,
        @Valid @RequestBody UpdateDeliveryRequest request
    ) {
        DeliveryDto dto = commandService.updateDelivery(request.toCommand(deliveryId));
        return ResponseEntity.ok(ApiResponse.success(DeliveryResponse.fromDeliveryDto(dto)));
    }

    @DeleteMapping("/{deliveryId}")
    public ResponseEntity<ApiResponse<Void>> deleteDelivery(@PathVariable UUID deliveryId) {
        commandService.deleteDelivery(deliveryId);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @GetMapping("/{deliveryId}")
    public ResponseEntity<ApiResponse<DeliveryResponse>> getDelivery(@PathVariable UUID deliveryId) {
        DeliveryDto dto = queryService.findById(deliveryId);
        return ResponseEntity.ok(ApiResponse.success(DeliveryResponse.fromDeliveryDto(dto)));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<DeliveryResponse>> getDeliveryByOrderId(@PathVariable UUID orderId) {
        DeliveryDto dto = queryService.findByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.success(DeliveryResponse.fromDeliveryDto(dto)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DeliveryResponse>>> searchDeliveries(
        @RequestParam(required = false) UUID hubId,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) UUID deliveryManagerId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "DESC") String sortDir
    ) {
        // 페이지 크기 제한 (10, 30, 50만 허용)
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<DeliveryDto> dtoPage = queryService.searchDeliveries(
            hubId,
            status != null ? com.pathfinder.delivery.domain.enums.DeliveryStatus.valueOf(status) : null,
            deliveryManagerId,
            pageable
        );

        Page<DeliveryResponse> responsePage = dtoPage.map(DeliveryResponse::fromDeliveryDto);
        return ResponseEntity.ok(ApiResponse.success(responsePage));
    }
}

package com.pathfinder.delivery_manager.presentation.controller;

import com.pathfinder.delivery_manager.application.DeliveryManagerServiceV1;
import com.pathfinder.delivery_manager.application.dto.request.DeliveryManagerRequestDto;
import com.pathfinder.delivery_manager.presentation.dto.request.DeliveryManagerRequest;
import com.pathfinder.delivery_manager.presentation.dto.response.DeliveryManagerResponseDto;
import com.pathfinder.global.presentation.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/delivery-managers")
public class DeliveryManagerControllerV1 {

    private final DeliveryManagerServiceV1 deliveryManagerService;

    public DeliveryManagerControllerV1(DeliveryManagerServiceV1 deliveryManagerService) {
        this.deliveryManagerService = deliveryManagerService;
    }

    @PostMapping
//    @PreAuthorize("hasRole('MASTER') or hasRole('HUB_MANAGER')")
    public ApiResponse<DeliveryManagerResponseDto> createManager(
            @Valid @RequestBody DeliveryManagerRequestDto requestDto) {
        return ApiResponse.success(deliveryManagerService.createDeliveryManager(requestDto));
    }

    // 단일 조회
    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('MASTER','HUB_MANAGER','DELIVERY_MANAGER')")
    public ApiResponse<DeliveryManagerResponseDto> getManager(@PathVariable Long id) {
        return ApiResponse.success(deliveryManagerService.getManager(id));
    }

    // 전체 목록 조회 + 검색
    @GetMapping
    public ApiResponse<Page<DeliveryManagerResponseDto>> getAllManagers(
            @RequestParam(required = false) Long hubId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "username") String sortBy,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc) {
        return ApiResponse.success(deliveryManagerService.getAllManagers(hubId, page, size, sortBy, isAsc));
    }

    // 수정
    @PutMapping("/{id}")
    public ApiResponse<DeliveryManagerResponseDto> updateManager(
            @PathVariable Long id,
            @Valid @RequestBody DeliveryManagerRequestDto requestDto) {
        requestDto.setDeliveryManagerId(id);
        return ApiResponse.success(deliveryManagerService.updateManager(requestDto));
    }

    // 삭제 (논리적 삭제)
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteManager(@PathVariable Long id) {
        deliveryManagerService.deleteManager(id);
        return ApiResponse.noContent();
    }
}
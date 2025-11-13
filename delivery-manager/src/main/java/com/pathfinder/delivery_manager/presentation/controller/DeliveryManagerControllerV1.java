package com.pathfinder.delivery_manager.presentation.controller;

import com.pathfinder.delivery_manager.application.DeliveryManagerServiceV1;
import com.pathfinder.delivery_manager.application.dto.request.DeliveryManagerRequestDto;
import com.pathfinder.delivery_manager.application.dto.request.DeliveryManagerUpdateRequestDto;
import com.pathfinder.delivery_manager.presentation.dto.response.DeliveryManagerResponseDto;
import com.pathfinder.global.presentation.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/delivery-managers")
@Tag(name = "배송담당자 API", description = "허브 및 업체 소속 배송담당자의 등록, 수정, 삭제, 조회 기능을 제공합니다.")
public class DeliveryManagerControllerV1 {

    private final DeliveryManagerServiceV1 deliveryManagerService;

    public DeliveryManagerControllerV1(DeliveryManagerServiceV1 deliveryManagerService) {
        this.deliveryManagerService = deliveryManagerService;
    }

    @PostMapping
    public ApiResponse<DeliveryManagerResponseDto> createManager(
            @Valid @RequestBody DeliveryManagerRequestDto requestDto) {
        return ApiResponse.success(deliveryManagerService.createDeliveryManager(requestDto));
    }

    @GetMapping("/id/{id}")
    public ApiResponse<DeliveryManagerResponseDto> getManagerByUsername(@PathVariable UUID id) {
        return ApiResponse.success(deliveryManagerService.getManagerById(id));
    }

    @GetMapping("/username/{username}")
    public ApiResponse<DeliveryManagerResponseDto> getManagerByUsername(@PathVariable String username) {
        return ApiResponse.success(deliveryManagerService.getManagerByUsername(username));
    }

    // 전체 목록 조회 + 검색
    @GetMapping
    public ApiResponse<Page<DeliveryManagerResponseDto>> getAllManagers(
            @RequestParam(required = false) UUID hubId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "status", defaultValue = "approve") String status,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc

    ) {
//        return ApiResponse.success(deliveryManagerService.getAllManagers(keyword, hubId, page, size, sortBy, status, isAsc, isDeleted));
        return ApiResponse.success(deliveryManagerService.getAllManagers(hubId, page, size, sortBy, isAsc));
    }

    // 수정
    @PutMapping("/{deliveryManagerId}")
    public ApiResponse<DeliveryManagerResponseDto> updateManager(
            @PathVariable UUID deliveryManagerId,
            @Valid @RequestBody DeliveryManagerUpdateRequestDto requestDto) {
        System.out.println("requestDto : " + requestDto);
        return ApiResponse.success(deliveryManagerService.updateManager(deliveryManagerId, requestDto));
    }

    // 삭제 (논리적 삭제)
    @DeleteMapping("/{deliveryManagerId}")
    public ApiResponse<Void> deleteManager(@PathVariable UUID deliveryManagerId) {
        deliveryManagerService.deleteManager(deliveryManagerId);
        return ApiResponse.noContent();
    }

}
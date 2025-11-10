package com.pathfinder.delivery_manager.presentation.controller;

import com.pathfinder.delivery_manager.application.DeliveryManagerServiceV1;
import com.pathfinder.delivery_manager.application.dto.request.DeliveryManagerRequestDto;
import com.pathfinder.delivery_manager.application.dto.request.DeliveryManagerUpdateRequestDto;
import com.pathfinder.delivery_manager.presentation.dto.response.DeliveryManagerResponseDto;
import com.pathfinder.global.presentation.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/delivery-managers")
public class DeliveryManagerControllerV1 {

    private final DeliveryManagerServiceV1 deliveryManagerService;

    public DeliveryManagerControllerV1(DeliveryManagerServiceV1 deliveryManagerService) {
        this.deliveryManagerService = deliveryManagerService;
    }

    @PostMapping
    @PreAuthorize("hasRole('MASTER') or hasRole('HUB_MANAGER')")
    public ApiResponse<DeliveryManagerResponseDto> createManager(
            @Valid @RequestBody DeliveryManagerRequestDto requestDto) {
        return ApiResponse.success(deliveryManagerService.createDeliveryManager(requestDto));
    }

    @GetMapping("/{id}")
    public ApiResponse<DeliveryManagerResponseDto> getManagerByUsername(@PathVariable Long id) {
        return ApiResponse.success(deliveryManagerService.getManagerById(id));
    }

    @GetMapping("/{username}")
    public ApiResponse<DeliveryManagerResponseDto> getManagerByUsername(@PathVariable String username) {
        return ApiResponse.success(deliveryManagerService.getManagerByUsername(username));
    }

    // 전체 목록 조회 + 검색
    @GetMapping
    public ApiResponse<Page<DeliveryManagerResponseDto>> getAllManagers(
          /*  @RequestParam(required = false) Long hubId,
            @RequestParam(value = "keyword", defaultValue = "name") String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "username") String sortBy,
            @RequestParam(value = "status", defaultValue = "approve") String status,
            @RequestParam(value = "isAsc", defaultValue = "false") boolean isAsc,
            @RequestParam(value = "isDeleted", defaultValue = "false") boolean isDeleted
*/
            @RequestParam(required = false) Long hubId,
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
            @PathVariable Long deliveryManagerId,
            @Valid @RequestBody DeliveryManagerUpdateRequestDto requestDto) {
        return ApiResponse.success(deliveryManagerService.updateManager(deliveryManagerId, requestDto));
    }

    // 삭제 (논리적 삭제)
    @DeleteMapping("/{deliveryManagerId}")
    public ApiResponse<Void> deleteManager(@PathVariable Long deliveryManagerId) {
        deliveryManagerService.deleteManager(deliveryManagerId);
        return ApiResponse.noContent();
    }
}
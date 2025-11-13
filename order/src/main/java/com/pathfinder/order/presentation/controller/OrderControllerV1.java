package com.pathfinder.order.presentation.controller;

import com.pathfinder.order.application.OrderServiceV1;
import com.pathfinder.order.application.dto.request.OrderCreateRequestDto;
import com.pathfinder.order.application.dto.request.OrderUpdateRequestDto;
import com.pathfinder.order.application.dto.response.OrderResponseDto;
import com.pathfinder.order.domain.enums.OrderStatus;
import com.pathfinder.order.presentation.dto.response.ApiResponseDto;
import com.pathfinder.order.presentation.enums.ApiStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderControllerV1 {

    private final OrderServiceV1 orderService;

    @PostMapping
    public ResponseEntity<ApiResponseDto<OrderResponseDto>> createOrder(@RequestBody OrderCreateRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails) {
        orderService.createOrder(requestDto);
        return ResponseEntity.status(ApiStatus.CREATED.getCode())
                .body(ApiResponseDto.success(ApiStatus.CREATED, "생성 완료"));
    }

    @DeleteMapping("/{orderId}/cancel")
    @PreAuthorize("hasAnyRole('MASTER','HUB_MANAGER')")
    public ResponseEntity<ApiResponseDto<OrderResponseDto>> deleteOrders(@PathVariable UUID orderId, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(ApiStatus.OK.getCode())
                .body(ApiResponseDto.success(ApiStatus.OK, orderService.cancelOrder(orderId), "주문이 취소되었습니다."));
    }

    @PutMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('MASTER','HUB_MANAGER')")
    public ResponseEntity<ApiResponseDto<OrderResponseDto>> updateOrder(@PathVariable UUID orderId, @RequestBody OrderUpdateRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(ApiStatus.OK.getCode())
                .body(ApiResponseDto.success(ApiStatus.OK, orderService.updateOrder(orderId, requestDto), "주문이 수정되었습니다."));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<OrderResponseDto>>> getOrders(
            @RequestParam(required = false, defaultValue = "") OrderStatus status,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {

        Page<OrderResponseDto> orders = orderService.getOrders(status, page, size);

        return ResponseEntity
                .status(ApiStatus.OK.getCode())
                .body(ApiResponseDto.success(ApiStatus.OK, orders, "주문 목록 조회 성공"));
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponseDto<OrderResponseDto>> getOrder(@PathVariable UUID orderId) {
        return ResponseEntity.status(ApiStatus.OK.getCode())
                .body(ApiResponseDto.success(ApiStatus.OK, orderService.getOrder(orderId), "주문 상세 조회 성공"));
    }

    @PutMapping("/{orderId}/delivery")
    public ResponseEntity<Void> setDelivery(@PathVariable UUID orderId, @RequestBody UUID deliveryId) {
        orderService.setDelivery(orderId, deliveryId);
        return ResponseEntity.ok().build();
    }
}

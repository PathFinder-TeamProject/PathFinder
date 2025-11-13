package com.pathfinder.delivery.infrastructure.external;

import com.pathfinder.delivery.infrastructure.external.dto.OrderDto;
import com.pathfinder.delivery.infrastructure.external.fallback.OrderServiceFallback;
import com.pathfinder.global.presentation.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(
    name = "order-service", 
    path = "/api/v1/orders",
    fallback = OrderServiceFallback.class
)
public interface OrderServiceClient {
    
    /**
     * 주문 조회
     * 실제 컨트롤러는 ResponseEntity<ApiResponseDto<OrderResponseDto>>를 반환
     * order-service는 status 필드를 사용하지만, 공통 ApiResponse는 code 필드를 사용
     * Jackson이 자동으로 매핑 (필드명이 다르면 매핑 실패 가능성 있음)
     */
    @GetMapping("/{orderId}")
    ApiResponse<OrderDto> getOrder(@PathVariable("orderId") UUID orderId);

    @PutMapping("/{orderId}/delivery")
    void delivery(@PathVariable("orderId") UUID orderId, @RequestBody UUID deliveryId);
}

package com.pathfinder.delivery.infrastructure.external.fallback;

import com.pathfinder.delivery.infrastructure.external.OrderServiceClient;
import com.pathfinder.delivery.infrastructure.external.dto.ApiResponseDto;
import com.pathfinder.delivery.infrastructure.external.dto.OrderDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class OrderServiceFallback implements OrderServiceClient {

    @Override
    public ResponseEntity<ApiResponseDto<OrderDto>> getOrder(UUID orderId) {
        log.error("Order Service Circuit Breaker activated for orderId: {}", orderId);
        return ResponseEntity.ok(new ApiResponseDto<>("ERROR", "Order service unavailable", null));
    }

    @Override
    public void delivery(UUID orderId, UUID deliveryId) {
        log.error("배송 지정 실패 orderId: {}", orderId);
    }
}


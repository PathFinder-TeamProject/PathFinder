package com.pathfinder.delivery.infrastructure.external.fallback;

import com.pathfinder.delivery.infrastructure.external.OrderServiceClient;
import com.pathfinder.delivery.infrastructure.external.dto.OrderDto;
import com.pathfinder.global.presentation.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class OrderServiceFallback implements OrderServiceClient {

    @Override
    public ApiResponse<OrderDto> getOrder(UUID orderId) {
        log.error("Order Service Circuit Breaker activated for orderId: {}", orderId);
        return null;
    }

    @Override
    public void delivery(UUID orderId, UUID deliveryId) {
        log.error("배송 지정 실패 orderId: {}", orderId);
    }
}


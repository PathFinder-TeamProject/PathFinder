package com.pathfinder.delivery.infrastructure.external.fallback;

import com.pathfinder.delivery.infrastructure.external.OrderServiceClient;
import com.pathfinder.delivery.infrastructure.external.dto.OrderDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@Profile("!local")
public class OrderServiceFallback implements OrderServiceClient {

    @Override
    public OrderDto getOrder(UUID orderId) {
        log.error("Order Service Circuit Breaker activated for orderId: {}", orderId);
        return null;
    }
}


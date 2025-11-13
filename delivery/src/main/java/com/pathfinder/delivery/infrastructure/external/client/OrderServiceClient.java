package com.pathfinder.delivery.infrastructure.external;

import com.pathfinder.delivery.infrastructure.external.dto.OrderDto;
import com.pathfinder.delivery.infrastructure.external.fallback.OrderServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
    name = "order-service", 
    path = "/internal/orders",
    fallback = OrderServiceFallback.class
)
public interface OrderServiceClient {
    
    @GetMapping("/{orderId}")
    OrderDto getOrder(@PathVariable("orderId") UUID orderId);
}

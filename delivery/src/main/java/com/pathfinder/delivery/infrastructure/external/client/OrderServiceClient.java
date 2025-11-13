package com.pathfinder.delivery.infrastructure.external;

import com.pathfinder.delivery.application.dto.response.DeliveryDto;
import com.pathfinder.delivery.infrastructure.external.dto.OrderDto;
import com.pathfinder.delivery.infrastructure.external.fallback.OrderServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.UUID;

@FeignClient(
    name = "order-service", 
    path = "/api/v1/orders",
    fallback = OrderServiceFallback.class
)
public interface OrderServiceClient {
    
    @GetMapping("/{orderId}")
    OrderDto getOrder(@PathVariable("orderId") UUID orderId);

    @PutMapping("{orderId}/delivery")
    void delivery(@PathVariable UUID orderId, UUID deliveryId);
}

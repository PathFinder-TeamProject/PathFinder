package com.pathfinder.order.infrastructure.global.client;

import com.pathfinder.order.infrastructure.global.dto.DeliveryDto;
import com.pathfinder.order.infrastructure.global.dto.ProductDto;
import com.pathfinder.order.infrastructure.global.fallback.CompanyClientFallback;
import com.pathfinder.order.infrastructure.global.fallback.DeliveryClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "company-service",
        path = "/v1/deliveries",
        fallback = DeliveryClientFallback.class
)
public interface DeliveryClient {
    @GetMapping("/{orderId}")
    DeliveryDto getDeliveryByOrderId(@PathVariable UUID orderId);
}

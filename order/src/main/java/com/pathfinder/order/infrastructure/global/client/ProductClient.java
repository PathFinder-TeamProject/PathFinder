package com.pathfinder.order.infrastructure.global.client;

import com.pathfinder.order.infrastructure.global.dto.ProductDto;
import com.pathfinder.order.infrastructure.global.fallback.ProductClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "product-service",
        path = "/v1/products",
        fallback = ProductClientFallback.class
)
public interface ProductClient {

    @GetMapping("/{productId}")
    ProductDto getProductById(@PathVariable UUID productId);
}

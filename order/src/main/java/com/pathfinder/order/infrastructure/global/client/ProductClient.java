package com.pathfinder.order.infrastructure.global.client;

import com.pathfinder.global.presentation.response.ApiResponse;
import com.pathfinder.order.infrastructure.global.dto.ProductDto;
import com.pathfinder.order.infrastructure.global.fallback.ProductClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(
        name = "product-service",
        path = "/api/v1/products",
        fallback = ProductClientFallback.class
)
public interface ProductClient {

    @GetMapping("/{productId}")
    ApiResponse<ProductDto> getProductById(@PathVariable UUID productId);

    @PutMapping("/{productId}/order")
    ApiResponse<Void> decreaseStock(@PathVariable UUID productId,@RequestParam("quantity") int quantity);
}

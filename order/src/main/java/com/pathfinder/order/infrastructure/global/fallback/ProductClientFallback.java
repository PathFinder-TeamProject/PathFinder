package com.pathfinder.order.infrastructure.global.fallback;

import com.pathfinder.global.presentation.response.ApiResponse;
import com.pathfinder.order.infrastructure.global.client.ProductClient;
import com.pathfinder.order.infrastructure.global.dto.ProductDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class ProductClientFallback implements ProductClient {

    @Override
    public ApiResponse<ProductDto> getProductById(UUID productId) {
        log.error("[Fallback] 상품 서비스 호출 실패 - productId: {}", productId);

        return null;
    }

    @Override
    public ApiResponse<Void> decreaseStock(UUID productId, int quantity) {
        log.error("[Fallback] 상품 재고 차감 실패 - productId: {}", productId);
        return null;
    }
}

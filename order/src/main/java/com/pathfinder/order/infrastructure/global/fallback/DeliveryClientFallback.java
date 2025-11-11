package com.pathfinder.order.infrastructure.global.fallback;

import com.pathfinder.order.infrastructure.global.client.DeliveryClient;
import com.pathfinder.order.infrastructure.global.dto.DeliveryDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class DeliveryClientFallback implements DeliveryClient {
    @Override
    public DeliveryDto getDeliveryByOrderId(UUID orderId) {
        log.error("[Fallback] 배송 호출 실패 - orderId: {}", orderId);
        return null;
    }
}

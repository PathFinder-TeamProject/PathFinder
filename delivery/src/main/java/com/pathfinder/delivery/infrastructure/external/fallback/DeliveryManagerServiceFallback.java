package com.pathfinder.delivery.infrastructure.external.fallback;

import com.pathfinder.delivery.infrastructure.external.DeliveryManagerServiceClient;
import com.pathfinder.delivery.infrastructure.external.dto.DeliveryManagerDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@Profile("!local")
public class DeliveryManagerServiceFallback implements DeliveryManagerServiceClient {

    @Override
    public DeliveryManagerDto getDeliveryManager(UUID deliveryManagerId) {
        log.error("Delivery Manager Service Circuit Breaker activated for deliveryManagerId: {}", deliveryManagerId);
        return null;
    }

    @Override
    public List<DeliveryManagerDto> getDeliveryManagersByHub(UUID hubId) {
        log.error("Delivery Manager Service Circuit Breaker activated for hubId: {}", hubId);
        return Collections.emptyList();
    }

    @Override
    public List<DeliveryManagerDto> getDeliveryManagersByHubAndType(UUID hubId, String type) {
        log.error("Delivery Manager Service Circuit Breaker activated for hubId: {}, type: {}", hubId, type);
        return Collections.emptyList();
    }
}


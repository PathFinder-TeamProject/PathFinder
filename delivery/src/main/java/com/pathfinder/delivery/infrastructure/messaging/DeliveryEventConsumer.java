package com.pathfinder.delivery.infrastructure.messaging;

import com.pathfinder.delivery.domain.event.DeliveryEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeliveryEventConsumer {

    @KafkaListener(topics = "delivery.events", groupId = "delivery-service-group")
    @CacheEvict(value = {"delivery", "deliveryRouteById", "deliveryRoutesByDeliveryId"}, allEntries = true)
    public void consumeDeliveryEvent(DeliveryEventDto event) {
        log.info("Consumed delivery event: deliveryId={}, eventType={}", event.getDeliveryId(), event.getEventType());
        // Read Model 업데이트 로직은 필요시 추가
        // 현재는 캐시 무효화만 수행
    }
}

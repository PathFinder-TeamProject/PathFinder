package com.pathfinder.delivery.infrastructure.messaging;

import com.pathfinder.delivery.domain.event.DeliveryDomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeliveryEventPublisher {

    private static final String DELIVERY_EVENTS_TOPIC = "delivery.events";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishDeliveryEvent(DeliveryDomainEvent event) {
        try {
            kafkaTemplate.send(DELIVERY_EVENTS_TOPIC, event.getDeliveryId().toString(), event);
            log.info("Published delivery event: deliveryId={}, eventType={}", event.getDeliveryId(), event.getEventType());
        } catch (Exception e) {
            log.error("Failed to publish delivery event: deliveryId={}", event.getDeliveryId(), e);
            throw new RuntimeException("Failed to publish delivery event", e);
        }
    }
}


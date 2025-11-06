package com.pathfinder.delivery_manager.kafka.producer;

import com.pathfinder.delivery_manager.kafka.dto.DeliveryManagerEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryManagerEventProducer {

    private final KafkaTemplate<String, DeliveryManagerEventDto> kafkaTemplate;

//    public void publish(DeliveryManagerEventDto event) {
//        kafkaTemplate.send("delivery-manager-events", event.getUsername(), event);
//    }
}
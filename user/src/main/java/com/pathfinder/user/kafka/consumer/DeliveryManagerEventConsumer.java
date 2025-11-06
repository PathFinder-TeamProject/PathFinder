package com.pathfinder.user.kafka.consumer;

import com.pathfinder.user.kafka.dto.DeliveryManagerEventDto;
import com.pathfinder.user.redis.DeliveryManagerCacheService;
import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryManagerEventConsumer {

    private final DeliveryManagerCacheService cacheService;
//
//    @KafkaListener(topics = "delivery-manager-events", groupId = "user-service-group")
//    public void consume(DeliveryManagerEventDto event) {
//        switch (event.getEventType()) {
//            case "CREATED", "UPDATED" ->
//                    cacheService.put(event.getUsername(), event.getHubId(), event.getType());
//            case "DELETED" ->
//                    cacheService.remove(event.getUsername());
//        }
//    }
}
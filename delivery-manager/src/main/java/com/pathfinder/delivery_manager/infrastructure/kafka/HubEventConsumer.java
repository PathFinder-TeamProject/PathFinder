package com.pathfinder.delivery_manager.infrastructure.kafka;

import com.pathfinder.delivery_manager.infrastructure.cache.HubCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventConsumer {

    private final HubCacheRepository hubCacheRepository;

//    @KafkaListener(topics = "hub-events", groupId = "delivery-manager-service")
//    public void consumeHubEvent(HubEvent event) {
//        switch (event.getAction()) {
//            case "CREATED" -> hubCacheRepository.add(event.getHubId());
//            case "DELETED" -> hubCacheRepository.remove(event.getHubId());
//        }
//        log.info("허브 캐시 갱신됨: {} - {}", event.getHubId(), event.getAction());
//    }
}
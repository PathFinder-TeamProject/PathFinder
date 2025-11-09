package com.pathfinder.delivery_manager.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathfinder.delivery_manager.application.DeliveryManagerServiceV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryManagerConsumer {
    private final ObjectMapper objectMapper;
    private final DeliveryManagerServiceV1 deliveryManagerService;

    private static final String TOPIC_NEW_DM = "user-new-delivery-manager-topic";
    private static final String GROUP_ID = "delivery-manager-registration-group";

    @KafkaListener(
            topics = TOPIC_NEW_DM,
            groupId = GROUP_ID
    )
    public void consumeNewDeliveryManagerEvent(ConsumerRecord<String, String> record) {
        String jsonMessage = record.value();

        try {
            // JSON 문자열을 이벤트 DTO 객체로 역직렬화
//            NewDeliveryManagerEvent event = objectMapper.readValue(jsonMessage, NewDeliveryManagerEvent.class);
//
//            log.info("[DM Service] Kafka 이벤트 수신 및 처리 시작 - User username: {}", event.getUsername());

            // 실제 배달 관리자 등록 비즈니스 로직 호출
//            deliveryManagerService.createDeliveryManager(event);

        } catch (Exception e) {
            log.error("Kafka 메시지 처리 중 오류 발생: Message={}", jsonMessage, e);
            // 메시지 소비 실패 시 처리 (DLQ, 알림 등)
        }
    }
}

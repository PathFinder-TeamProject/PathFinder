package com.pathfinder.user.kafka;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathfinder.global.event.NewDeliveryManagerEvent;
import com.pathfinder.user.application.dto.request.SignupRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    // Kafka Topic 이름
    private static final String TOPIC_NEW_DELIVERY_MANAGER = "user-new-delivery-manager-topic";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    public void publishNewDeliveryManagerEvent(NewDeliveryManagerEvent event) {
        try {
            // DTO 객체를 JSON 문자열로 직렬화
            String jsonMessage = objectMapper.writeValueAsString(event);

            // Key는 username으로 설정 (같은 사용자의 메시지는 순서 보장)
            String key = event.getUsername();

            // Kafka 메시지 발행
            kafkaTemplate.send(TOPIC_NEW_DELIVERY_MANAGER, key, jsonMessage);

            log.info("[User Service] Kafka 이벤트 발행 완료 - Topic: {}, Username: {}",
                    TOPIC_NEW_DELIVERY_MANAGER, key);

        } catch (JsonProcessingException e) {
            log.error("[User Service] Kafka 메시지 직렬화 실패 - Username: {}, Error: {}",
                    event.getUsername(), e.getMessage());
            throw new RuntimeException("Kafka 메시지 전송 실패", e);
        } catch (Exception e) {
            log.error("[User Service] Kafka 메시지 발행 중 오류 - Username: {}, Error: {}",
                    event.getUsername(), e.getMessage());
            throw new RuntimeException("Kafka 메시지 전송 실패", e);
        }
    }
}
package com.pathfinder.user.kafka;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathfinder.user.application.dto.request.SignupRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    // 메시지를 보낼 Kafka Topic 이름
    private static final String TOPIC_NEW_DELIVERY_MANAGER = "user-new-delivery-manager-topic";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper; // DTO 객체를 JSON 문자열로 변환하기 위해 필요

    /*public void publishNewDeliveryManagerEvent(NewDeliveryManagerEvent event) {
        try {
            // DTO 객체를 JSON 문자열로 직렬화
            String jsonMessage = objectMapper.writeValueAsString(event);

            // Key는 메시지의 순서를 보장하고 싶은 단위
            String key = event.getUsername();

            // 메시지 발행
            kafkaTemplate.send(TOPIC_NEW_DELIVERY_MANAGER, key, jsonMessage);

            log.info("Kafka 메시지 발행 요청 완료 - Topic: {}, User: {}", TOPIC_NEW_DELIVERY_MANAGER, key);

        } catch (JsonProcessingException e) {
            log.error("NewDeliveryManagerEvent 직렬화 오류: {}", e.getMessage());
            throw new RuntimeException("Kafka 메시지 전송 오류", e);
        }
    }*/
}
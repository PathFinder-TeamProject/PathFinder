package com.pathfinder.delivery_manager.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathfinder.delivery_manager.application.DeliveryManagerServiceV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryManagerConsumer {

    private final ObjectMapper objectMapper;
    private final DeliveryManagerServiceV1 deliveryManagerService;

    // Kafka Topic 및 Consumer Group 설정
    private static final String TOPIC_NEW_DM = "user-new-delivery-manager-topic";
    private static final String GROUP_ID = "delivery-manager-registration-group";

    /**
     * 새로운 배송 담당자 등록 이벤트 수신 및 처리
     */
    /*
    @KafkaListener(
            topics = TOPIC_NEW_DM,
            groupId = GROUP_ID
    )
    public void consumeNewDeliveryManagerEvent(ConsumerRecord<String, String> record,
                                               Acknowledgment acknowledgment) {

        String key = record.key();
        String jsonMessage = record.value();

        log.info("[DM Service] Kafka 메시지 수신 - Key: {}, Topic: {}", key, TOPIC_NEW_DM);

        try {
            // JSON 문자열을 이벤트 DTO 객체로 역직렬화
            NewDeliveryManagerEvent event = objectMapper.readValue(
                    jsonMessage,
                    NewDeliveryManagerEvent.class
            );

            log.info("[DM Service] 이벤트 파싱 완료 - Username: {}, Type: {}, HubId: {}",
                    event.getUsername(), event.getDeliveryManagerType(), event.getHubId());

            // NewDeliveryManagerEvent → DeliveryManagerRequestDto 변환
            DeliveryManagerRequestDto requestDto = DeliveryManagerRequestDto.builder()
                    .username(event.getUsername())
                    .hubId(event.getHubId())
                    .type(DeliveryManagerTypeEnum.valueOf(event.getDeliveryManagerType()))
                    .deliveryOrder(null)  // 서비스에서 자동 계산
                    .build();

            // 기존 서비스 로직 호출
            deliveryManagerService.createDeliveryManager(requestDto);

            acknowledgment.acknowledge();
            log.info("[DM Service] 배송 담당자 등록 완료 - Username: {}", event.getUsername());

        } catch (Exception e) {
            log.error("[DM Service] Kafka 메시지 처리 중 오류 발생 - Key: {}, Message: {}, Error: {}",
                    key, jsonMessage, e.getMessage(), e);

            // TODO: 실패한 메시지를 DLQ(Dead Letter Queue)로 전송하거나
            // TODO: 재처리 로직 구현
            // TODO: 알림 발송 등의 오류 처리
        }
    }*/
}
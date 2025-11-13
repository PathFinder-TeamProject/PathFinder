package com.pathfinder.delivery.infrastructure.messaging;

import com.pathfinder.delivery.domain.event.DeliveryEventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeliveryEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private DeliveryEventPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new DeliveryEventPublisher(kafkaTemplate);
    }

    @Test
    @DisplayName("배송 이벤트 발행 시 Kafka에 이벤트를 전송한다")
    void publishDeliveryEvent_shouldSendEventToKafka() {
        // given
        UUID deliveryId = UUID.randomUUID();
        DeliveryEventDto event = DeliveryEventDto.builder()
                .deliveryId(deliveryId)
                .eventType("CREATED")
                .build();

        // when
        publisher.publishDeliveryEvent(event);

        // then
        ArgumentCaptor<DeliveryEventDto> eventCaptor = ArgumentCaptor.forClass(DeliveryEventDto.class);
        verify(kafkaTemplate).send(eq("delivery.events"), eq(deliveryId.toString()), eventCaptor.capture());
        
        DeliveryEventDto capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.getDeliveryId()).isEqualTo(deliveryId);
        assertThat(capturedEvent.getEventType()).isEqualTo("CREATED");
    }

    @Test
    @DisplayName("배송 이벤트 발행 시 deliveryId를 키로 사용한다")
    void publishDeliveryEvent_shouldUseDeliveryIdAsKey() {
        // given
        UUID deliveryId = UUID.randomUUID();
        DeliveryEventDto event = DeliveryEventDto.builder()
                .deliveryId(deliveryId)
                .eventType("UPDATED")
                .build();

        // when
        publisher.publishDeliveryEvent(event);

        // then
        verify(kafkaTemplate).send(eq("delivery.events"), eq(deliveryId.toString()), eq(event));
    }
}


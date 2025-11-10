package com.pathfinder.delivery.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathfinder.delivery.domain.entity.DeliveryOutboxEntity;
import com.pathfinder.delivery.domain.enums.DeliveryOutboxStatus;
import com.pathfinder.delivery.domain.event.DeliveryEventDto;
import com.pathfinder.delivery.domain.repository.DeliveryOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryOutboxPublisher {

    private final DeliveryOutboxRepository outboxRepository;
    private final DeliveryEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    @Value("${delivery.outbox.publisher.batch-size:50}")
    private int batchSize;

    @Value("${delivery.outbox.publisher.max-retries:5}")
    private int maxRetries;

    @Scheduled(fixedDelayString = "${delivery.outbox.publisher.poll-interval:5000}")
    @Transactional
    public void publishPendingEvents() {
        List<DeliveryOutboxEntity> pendingEvents =
            outboxRepository.findByStatusWithLock(DeliveryOutboxStatus.PENDING, batchSize);

        if (pendingEvents.isEmpty()) {
            return;
        }

        pendingEvents.forEach(event -> {
            event.markProcessing();
            try {
                DeliveryEventDto domainEvent =
                    objectMapper.readValue(event.getPayload(), DeliveryEventDto.class);
                eventPublisher.publishDeliveryEvent(domainEvent);
                event.markPublished();
            } catch (Exception ex) {
                boolean finalFailure = event.getRetryCount() >= maxRetries;
                event.markFailed(ex.getMessage(), finalFailure);
                log.error("Failed to publish delivery outbox event. outboxId={}, finalFailure={}",
                    event.getOutboxId(), finalFailure, ex);
            }
        });
    }
}


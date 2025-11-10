package com.pathfinder.delivery.application.outbox.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathfinder.delivery.application.outbox.DeliveryOutboxService;
import com.pathfinder.delivery.domain.entity.DeliveryOutboxEntity;
import com.pathfinder.delivery.domain.event.DeliveryEventDto;
import com.pathfinder.delivery.domain.repository.DeliveryOutboxRepository;
import com.pathfinder.delivery.domain.error.DeliveryErrorCode;
import com.pathfinder.global.presentation.exception.PathException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryOutboxServiceImpl implements DeliveryOutboxService {

    private final DeliveryOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void enqueue(DeliveryEventDto event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            DeliveryOutboxEntity outboxEntity = event.toOutboxEntity(payload);
            outboxRepository.save(outboxEntity);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize delivery event for outbox: deliveryId={}, eventType={}", event.getDeliveryId(), event.getEventType(), e);
            throw new PathException(DeliveryErrorCode.OUTBOX_SERIALIZATION_FAILED, e);
        }
    }
}


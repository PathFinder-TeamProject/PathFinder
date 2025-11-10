package com.pathfinder.delivery.application.outbox;

import com.pathfinder.delivery.domain.event.DeliveryEventDto;

public interface DeliveryOutboxService {
    void enqueue(DeliveryEventDto event);
}


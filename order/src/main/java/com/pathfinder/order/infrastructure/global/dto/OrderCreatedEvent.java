package com.pathfinder.order.infrastructure.global.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedEvent {
        private UUID orderId;
        private UUID productId;
        private long quantity;
}

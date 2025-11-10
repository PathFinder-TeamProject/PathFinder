package com.pathfinder.order.presentation.dto.response;

import com.pathfinder.order.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class OrderResponseDto {
    private UUID id;

    private UUID productId;

    private UUID supplierId;

    private UUID receiverId;

    private UUID deliveryId;

    private OrderStatus orderStatus;

    private long quantity;

    private String request;

    private Timestamp deadline;
}

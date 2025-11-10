package com.pathfinder.order.presentation.dto.request;

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
public class OrderCreateRequestDto {
    private UUID productId;

    private UUID supplierId;

    private UUID receiverId;

    private long quantity;

    private String request;

    private Timestamp deadline;
}

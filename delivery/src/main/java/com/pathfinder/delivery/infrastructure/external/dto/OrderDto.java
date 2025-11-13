package com.pathfinder.delivery.infrastructure.external.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private UUID id;  // order-service의 OrderResponseDto.id와 매핑
    private UUID productId;
    private UUID supplierId;  // order-service의 OrderResponseDto.supplierId와 매핑
    private UUID receiverId;  // order-service의 OrderResponseDto.receiverId와 매핑
    private UUID deliveryId;
    private String orderStatus;  // OrderStatus Enum을 String으로 받음 (JSON 역직렬화 시 자동 변환)
    private long quantity;  // OrderResponseDto.quantity (long)와 일치
    private String request;
    private java.sql.Timestamp deadline;
}


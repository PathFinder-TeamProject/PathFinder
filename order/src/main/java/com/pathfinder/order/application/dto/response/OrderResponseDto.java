package com.pathfinder.order.application.dto.response;

import com.pathfinder.order.domain.entity.OrderEntity;
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

    public OrderResponseDto fromEntity(OrderEntity order) {
        return OrderResponseDto.builder()
                .id(order.getId())
                .productId(order.getProductId())
                .supplierId(order.getSupplierId())
                .receiverId(order.getReceiverId())
                .deliveryId(order.getDeliveryId())
                .orderStatus(order.getOrderStatus())
                .quantity(order.getQuantity())
                .request(order.getRequest())
                .deadline(order.getDeadline())
                .build();
    }
}



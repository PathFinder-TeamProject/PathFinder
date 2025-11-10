package com.pathfinder.order.domain.entity;

import com.pathfinder.global.infrastructure.entity.BaseEntity;
import com.pathfinder.order.application.dto.request.OrderUpdateRequestDto;
import com.pathfinder.order.domain.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@Table(name = "p_order")
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private UUID supplierId;

    @Column(nullable = false)
    private UUID receiverId;

    private UUID deliveryId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    private long quantity;

    @Column(nullable = false)
    private String request;

    @Column(nullable = false)
    private Timestamp deadline;

    public void changeStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void update(OrderUpdateRequestDto requestDto) {
        if (requestDto.getRequest() != null) {
            this.request = requestDto.getRequest();
        }

        if (requestDto.getQuantity() != 0) {
            this.quantity = requestDto.getQuantity();
        }

        if (requestDto.getDeadline() != null) {
            this.deadline = requestDto.getDeadline();
        }
    }
}

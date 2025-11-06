package com.pathfinder.delivery.domain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "p_deliveries")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private String recipientName;
    private String recipientPhone;
    private String address;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void updateDelivery(String recipientName, String recipientPhone, 
                               String address, String status) {
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.address = address;
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
}


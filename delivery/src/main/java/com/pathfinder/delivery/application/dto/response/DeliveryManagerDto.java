package com.pathfinder.delivery.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryManagerDto {
    private UUID deliveryManagerId;
    private UUID userId;
    private String type;
    private Integer deliveryOrder;
    private UUID hubId;
}


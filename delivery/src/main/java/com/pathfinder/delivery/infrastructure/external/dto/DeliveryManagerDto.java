package com.pathfinder.delivery.infrastructure.external.dto;

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
    private Long deliveryManagerId;
    private String username;
    private String type;
    private Integer deliveryOrder;
    private UUID hubId;
    private String slackId;
}


package com.pathfinder.delivery.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeliveryRequest {
    private Long orderId;
    private String recipientName;
    private String recipientPhone;
    private String address;
}


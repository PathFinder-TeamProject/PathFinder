package com.pathfinder.delivery.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDeliveryRequest {
    private String recipientName;
    private String recipientPhone;
    private String address;
    private String status;
}


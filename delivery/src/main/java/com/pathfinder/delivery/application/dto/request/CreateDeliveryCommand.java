package com.pathfinder.delivery.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeliveryCommand {
    private Long orderId;
    private String recipientName;
    private String recipientPhone;
    private String address;
}


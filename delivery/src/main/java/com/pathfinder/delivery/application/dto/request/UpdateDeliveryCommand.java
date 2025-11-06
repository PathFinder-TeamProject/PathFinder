package com.pathfinder.delivery.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDeliveryCommand {
    private Long id;
    private String recipientName;
    private String recipientPhone;
    private String address;
    private String status;
}


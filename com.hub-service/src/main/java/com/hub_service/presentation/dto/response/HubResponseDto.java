package com.hub_service.presentation.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HubResponseDto {
    private UUID hubId;
    private String hubName;
    private String hubAddress;
    private double latitude;
    private double longitude;
}

package com.hub_service.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
public class HubResponseDto {
    private UUID hubId;
    private String hubName;
    private String hubAddress;
    private double latitude;
    private double longitude;
}

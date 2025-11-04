package com.hub_service.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HubResponseDto {
    private String hubId;
    private String hubName;
    private String hubAddress;
    private double latitude;
    private double longitude;
}

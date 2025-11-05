package com.hub_service.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
public class HubResponseDto {
    private UUID id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
}

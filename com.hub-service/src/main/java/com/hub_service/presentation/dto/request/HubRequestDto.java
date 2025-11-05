package com.hub_service.presentation.dto.request;

import lombok.Data;

@Data
public class HubRequestDto {
    private String hubName;
    private String hubAddress;
    private double latitude;
    private double longitude;
}

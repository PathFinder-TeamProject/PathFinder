package com.hub_service.presentation.dto.request;

import lombok.Data;

@Data
public class HubRequestDto {
    private String name;
    private String address;
    private double latitude;
    private double longitude;
}

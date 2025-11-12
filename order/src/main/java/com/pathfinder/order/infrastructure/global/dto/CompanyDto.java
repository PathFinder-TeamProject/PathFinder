package com.pathfinder.order.infrastructure.global.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    private UUID companyId;
    private String companyName;
    private String businessNo;
    private String address;
    private String manager;
    private UUID hubId;
}


package com.pathfinder.order.infrastructure.global.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private UUID productId;
    private String productName;
    private UUID hubId;
    private UUID companyId;
    private Integer stock;
    private Double price;
}

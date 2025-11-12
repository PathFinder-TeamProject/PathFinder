package com.pathfinder.order.infrastructure.global.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private UUID productId;
    private String productName;
    private String category;
    private Integer stock;
    private UUID companyId;
}

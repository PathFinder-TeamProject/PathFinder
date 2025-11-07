package com.pathfinder.delivery_manager.presentation.dto.response;

import com.pathfinder.delivery_manager.domain.enums.DeliveryManagerTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryManagerResponseDto {
    private Long deliveryManagerId;
    private String username;
    private Long hubId;
    private DeliveryManagerTypeEnum type;
    private int deliveryOrder;
    private LocalDateTime createdAt;
    private String createdBy;
}
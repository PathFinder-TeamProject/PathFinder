package com.pathfinder.delivery_manager.application.dto.request;

import com.pathfinder.delivery_manager.domain.enums.DeliveryManagerTypeEnum;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryManagerRequestDto {
    private String username;

    private Long hubId;

    private DeliveryManagerTypeEnum type;

    private Integer deliveryOrder;
}

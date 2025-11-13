package com.pathfinder.delivery_manager.application.dto.request;

import com.pathfinder.delivery_manager.domain.enums.DeliveryManagerTypeEnum;
import com.pathfinder.delivery_manager.presentation.dto.response.DeliveryManagerResponseDto;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DeliveryManagerRequestDto {

    private UUID deliveryManagerId;

    private String username;

    private UUID hubId;

    private DeliveryManagerTypeEnum type;

    private Integer deliveryOrder;

}

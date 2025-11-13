package com.pathfinder.delivery_manager.application.dto.request;

import com.pathfinder.delivery_manager.domain.enums.DeliveryManagerTypeEnum;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DeliveryManagerCreateRequestDto {

    private String username;

    private UUID hubId;

    private DeliveryManagerTypeEnum type;

    private int deliveryOrder;

}

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
public class DeliveryManagerUpdateRequestDto {

    private UUID hubId;

    private DeliveryManagerTypeEnum type;
}

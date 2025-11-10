package com.pathfinder.delivery_manager.application.dto.request;

import com.pathfinder.delivery_manager.domain.enums.DeliveryManagerTypeEnum;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DeliveryManagerUpdateRequestDto {

    private Long hubId;

    private DeliveryManagerTypeEnum type;
}

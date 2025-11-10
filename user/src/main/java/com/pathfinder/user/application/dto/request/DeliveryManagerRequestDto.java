package com.pathfinder.user.application.dto.request;

import com.pathfinder.user.domain.enums.DeliveryManagerTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryManagerRequestDto {
    private String username;
    private Long hubId;
    private DeliveryManagerTypeEnum deliveryManagerType;
}

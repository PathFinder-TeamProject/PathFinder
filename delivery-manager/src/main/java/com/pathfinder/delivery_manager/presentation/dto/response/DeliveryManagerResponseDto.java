package com.pathfinder.delivery_manager.presentation.dto.response;

import com.pathfinder.delivery_manager.domain.entity.DeliveryManagerEntity;
import com.pathfinder.delivery_manager.domain.enums.DeliveryManagerTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryManagerResponseDto {

    private UUID deliveryManagerId;
    private String username;
    private Integer deliveryOrder;
    private DeliveryManagerTypeEnum type;

    private HubInfoDto hubInfo;
    private UserInfoDto userInfo;

    // 리스트용 DTO
    public static DeliveryManagerResponseDto forList(DeliveryManagerEntity deliveryManager) {
        HubInfoDto hubInfo = HubInfoDto.builder()
                .hubId(deliveryManager.getHubId())
                .build();

        UserInfoDto userInfo = UserInfoDto.builder()
                .username(deliveryManager.getUsername())
                .build();

        return DeliveryManagerResponseDto.builder()
                .deliveryManagerId(deliveryManager.getDeliveryManagerId())
                .username(deliveryManager.getUsername())
                .deliveryOrder(deliveryManager.getDeliveryOrder())
                .type(deliveryManager.getType())
/*              .deletedAt(user.getDeletedAt())
                .isDeleted(user.getIsDeleted())*/
                .hubInfo(hubInfo)
                .userInfo(userInfo)
                .build();
    }

    // 상세보기용 DTO (추후 UserService, HubService 연동 시 확장)
    public static DeliveryManagerResponseDto of(DeliveryManagerEntity deliveryManager, UserInfoDto userInfoDto) {
        HubInfoDto hubInfo = HubInfoDto.builder()
                .hubId(deliveryManager.getHubId())
                .build();

        return DeliveryManagerResponseDto.builder()
                .username(userInfoDto.getUsername())
                .deliveryManagerId(deliveryManager.getDeliveryManagerId())
                .hubInfo(hubInfo)
                .userInfo(userInfoDto)
                .deliveryOrder(deliveryManager.getDeliveryOrder())
                .type(deliveryManager.getType())
                    /* .deletedAt(user.getDeletedAt())
                .isDeleted(user.getIsDeleted())*/
                .build();
    }
}

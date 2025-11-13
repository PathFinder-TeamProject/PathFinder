package com.pathfinder.delivery_manager.application.dto.request;

import com.pathfinder.delivery_manager.domain.enums.DeliveryManagerTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "배송담당자 등록 요청 DTO")
public class DeliveryManagerCreateRequestDto {

    @Schema(description = "유저 ID (4~10자, 소문자+숫자)", example = "sparta123", required = true)
    private String username;

    @Schema(description = "소속 허브 ID", example = "(만약 허브 배송 담당자 일 경우 00000000-0000-0000-0000-000000000000) ", required = true)
    private UUID hubId;

    @Schema(description = "배송 담당자 유형 (HUB, COMPANY 중 하나)", example = "COMPANY", required = true)
    private DeliveryManagerTypeEnum type;
}

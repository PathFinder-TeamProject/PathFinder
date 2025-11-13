package com.pathfinder.user.application.dto.request;

import com.pathfinder.user.domain.enums.UserRoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "회원 역할 수정 DTO")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserRoleUpdateRequestDto {
    @Schema(description = "사용자 역할 (MASTER, HUB_MANAGER, DELIVERY_MANAGER, COMPANY_MANAGER 중 하나)", example = "HUB_MANAGER", required = true)
    @NotNull(message = "역할(role)은 필수 입력 값입니다.")
    private UserRoleEnum role;
}

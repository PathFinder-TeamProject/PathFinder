package com.pathfinder.user.application.dto.request;
import com.pathfinder.user.domain.enums.UserStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Schema(description = "회원 승인 상태 수정 DTO")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserStatusUpdateRequestDto {
    @Schema(description = "사용자 상태값  (APPROVED, REJECTED, PENDING 중 하나)", example = "PENDING", required = true)
    @NotNull(message = "상태값은 필수 입력 값입니다.")
    private UserStatusEnum status;
}

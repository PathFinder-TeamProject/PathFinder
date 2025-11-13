package com.pathfinder.user.application.dto.request;

import com.pathfinder.user.domain.enums.UserRoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "회원가입 요청 DTO")
@Getter
@Setter
@Builder
public class SignupRequestDto {

    @Schema(description = "사용자 이메일", example = "user@example.com", required = true)
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @Schema(description = "로그인 ID (4~10자, 소문자+숫자)", example = "sparta123", required = true)
    @NotBlank(message = "username은 필수 입력 값입니다.")
    @Pattern(
            regexp = "^[a-z0-9]{4,10}$",
            message = "username은 최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0-9)로 구성되어야 합니다."
    )
    private String username;

    @Schema(description = "사용자 이름", example = "김스파르타", required = true)
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @Schema(description = "비밀번호 (8~15자, 대소문자+숫자+특수문자 포함)", example = "Abcd1234!", required = true)
    @NotBlank(message = "password는 필수 입력 값입니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "password는 최소 8자 이상, 15자 이하이며, 알파벳 대소문자(a~z, A-Z), 숫자(0-9), 특수문자(@$!%*?&)를 모두 포함해야 합니다."
    )
    private String password;

    @Schema(description = "소속 허브 또는 업체명", example = "경기남부 허브", required = true)
    @NotBlank(message = "소속(organization)은 필수 입력 값입니다.")
    private String organization;

    @Schema(description = "사용자 역할 (MASTER, HUB_MANAGER, DELIVERY_MANAGER, COMPANY_MANAGER 중 하나)", example = "HUB_MANAGER", required = true)
    @NotNull(message = "역할(role)은 필수 입력 값입니다.")
    private UserRoleEnum role;

    @Schema(description = "사용자 슬랙 ID", example = "U12345678", required = true)
    @NotNull(message = "슬랙 아이디(slackId)는 필수 입력 값입니다.")
    private String slackId;
}

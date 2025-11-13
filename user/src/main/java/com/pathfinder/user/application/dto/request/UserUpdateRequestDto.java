package com.pathfinder.user.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "회원 정보 수정 요청 DTO")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserUpdateRequestDto {
    @Schema(description = "사용자 이메일", example = "user@example.com", required = false)
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @Schema(description = "사용자 이름", example = "김스파르타", required = false)
    private String name;

    @Schema(description = "비밀번호 (8~15자, 대소문자+숫자+특수문자 포함)", example = "Abcd1234!", required = false)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "password는 최소 8자 이상, 15자 이하이며, 알파벳 대소문자(a~z, A-Z), 숫자(0-9), 특수문자(@$!%*?&)를 모두 포함해야 합니다."
    )
    private String password;

    @Schema(description = "새 비밀번호 (8~15자, 대소문자+숫자+특수문자 포함)", example = "Abcd1234!", required = false)
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "password는 최소 8자 이상, 15자 이하이며, 알파벳 대소문자(a~z, A-Z), 숫자(0-9), 특수문자(@$!%*?&)를 모두 포함해야 합니다."
    )
    private String newPassword;
    
    @Schema(description = "소속 허브 또는 업체명", example = "경기남부 허브", required = false)
    private String organization;
    
    @Schema(description = "사용자 슬랙 ID", example = "U12345678", required = false)
    private String slackId;
}

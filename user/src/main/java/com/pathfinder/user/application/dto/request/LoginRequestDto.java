package com.pathfinder.user.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Schema(description = "로그인 요청 DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDto {
    @Schema(description = "로그인 ID (4~10자, 소문자+숫자)", example = "sparta123", required = true)
    @NotBlank(message = "username은 필수 입력 값입니다.")
    @Pattern(
            regexp = "^[a-z0-9]{4,10}$",
            message = "username은 최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0-9)로 구성되어야 합니다."
    )
    private String username;

    @Schema(description = "비밀번호 (8~15자, 대소문자+숫자+특수문자 포함)", example = "Abcd1234!", required = true)
    @NotBlank
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "password는 최소 8자 이상, 15자 이하이며, 알파벳 대소문자(a~z, A-Z), 숫자(0-9), 특수문자(@$!%*?&)를 모두 포함해야 합니다."
    )
    private String password;
}

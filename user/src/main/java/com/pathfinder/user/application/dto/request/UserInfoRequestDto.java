package com.pathfinder.user.application.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "회원 정보 수정 DTO")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserInfoRequestDto {
    @Schema(description = "사용자 이름", example = "김스파르타", required = true)
    private String name;
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;
}
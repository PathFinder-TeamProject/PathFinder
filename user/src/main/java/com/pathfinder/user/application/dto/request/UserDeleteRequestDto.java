package com.pathfinder.user.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserDeleteRequestDto {

    @NotBlank
    private String username;

    @NotBlank
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "password는 최소 8자 이상, 15자 이하이며, 알파벳 대소문자(a~z, A-Z), 숫자(0-9), 특수문자(@$!%*?&)를 모두 포함해야 합니다."
    )
    private String password;
}

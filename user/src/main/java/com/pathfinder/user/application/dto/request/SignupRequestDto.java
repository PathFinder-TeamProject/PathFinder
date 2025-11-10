package com.pathfinder.user.application.dto.request;

import com.pathfinder.user.domain.enums.DeliveryManagerTypeEnum;
import com.pathfinder.user.domain.enums.UserRoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Builder
public class SignupRequestDto {
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "username은 필수 입력 값입니다.")
    @Pattern(
            regexp = "^[a-z0-9]{4,10}$",
            message = "username은 최소 4자 이상, 10자 이하이며 알파벳 소문자(a~z), 숫자(0-9)로 구성되어야 합니다."
    )
    private String username;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotBlank(message = "password는 필수 입력 값입니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "password는 최소 8자 이상, 15자 이하이며, 알파벳 대소문자(a~z, A-Z), 숫자(0-9), 특수문자(@$!%*?&)를 모두 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "소속(organization)은 필수 입력 값입니다.")
    private String organization;

    @NotNull(message = "역할(role)은 필수 입력 값입니다.")
    private UserRoleEnum role;

    private DeliveryManagerTypeEnum deliveryManagerType;

    @NotNull(message = "슬랙 아이디(slackId)는 필수 입력 값입니다.")
    private String slackId;

    @NotNull(message = "허브 아이디(hubId)는 필수 입력 값입니다.")
    private Long hubId;
}

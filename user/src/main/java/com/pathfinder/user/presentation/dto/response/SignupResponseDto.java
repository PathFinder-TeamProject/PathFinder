package com.pathfinder.user.presentation.dto.response;

import com.pathfinder.user.domain.entity.UserEntity;
import com.pathfinder.user.domain.enums.UserRoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class SignupResponseDto {

    private String username;

    private String email;

    private String name;

    private String organization;

    private String eventType;

    private String slackId;

    private UserRoleEnum role;

    private Long hubId;

    public static SignupResponseDto of(UserEntity userEntity) {
        return SignupResponseDto.builder()
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .name(userEntity.getName())
                .role(userEntity.getRole())
                .organization(userEntity.getOrganization())
                .slackId(userEntity.getSlackId())
                .build();
    }
}

package com.pathfinder.user.presentation.dto.response;

import com.pathfinder.user.domain.entity.UserEntity;
import com.pathfinder.user.domain.enums.UserRoleEnum;
import com.pathfinder.user.domain.enums.UserStatusEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public class UserResponseDto {
    private String username;

    private String email;

    private String name;

    private String organization;

    private String eventType;

    private String slackId;

    private UserRoleEnum role;

    private Long hubId;

    private UserStatusEnum status;

    private Instant createdAt;

    private boolean isDeleted;

    public static UserResponseDto of(UserEntity user) {
        return UserResponseDto.builder()
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .organization(user.getOrganization())
                .slackId(user.getSlackId())
                .hubId(user.getHubId())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .isDeleted(user.getDeletedBy()==null? false:true)
                .build();
    }
}

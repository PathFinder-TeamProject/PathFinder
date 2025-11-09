package com.pathfinder.user.presentation.dto.response;

import com.pathfinder.user.domain.entity.UserEntity;
import com.pathfinder.user.domain.enums.*;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public class UserStatusUpdateResponseDto {
    private String username;

    private String email;

    private String name;

    private UserRoleEnum role;

    private LocalDateTime updatedAt;

    private boolean isDeleted;

    private UserStatusEnum status;

    public static UserStatusUpdateResponseDto of(UserEntity user) {
        return UserStatusUpdateResponseDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .status(user.getStatus())
/*                .updatedAt(user.getUpdatedAt())
                .updatedBy(user.getUpdatedBy())
                .isDeleted(user.getIsDeleted())
                */
                .build();
    }
}

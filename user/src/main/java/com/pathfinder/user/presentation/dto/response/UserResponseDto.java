package com.pathfinder.user.presentation.dto.response;

import com.pathfinder.user.domain.entity.UserEntity;
import com.pathfinder.user.domain.enums.UserRoleEnum;
import com.pathfinder.user.domain.enums.UserStatusEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponseDto {

    private String username;

    private String name;

    private String email;

    private UserRoleEnum role;

    private UserStatusEnum status;

    private LocalDateTime createdAt;

    private boolean isDeleted;

    public static UserResponseDto of(UserEntity user) {
        return UserResponseDto.builder()
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
//                .createdAt(user.getCreatedAt())
//                .isDeleted(user.getIsDeleted())
                .build();
    }
}

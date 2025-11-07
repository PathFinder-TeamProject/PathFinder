package com.pathfinder.user.presentation.dto.response;

import com.pathfinder.user.domain.entity.UserEntity;
import com.pathfinder.user.domain.enums.UserRoleEnum;
import com.pathfinder.user.domain.enums.UserRoleEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserRoleUpdateResponseDto {

    private String username;

    private String email;

    private String name;

    private UserRoleEnum role;

    private LocalDateTime updatedAt;

    private String updatedBy;

    private boolean isDeleted;

//    private UserStatusEnum status;

    public static UserRoleUpdateResponseDto of(UserEntity user) {
        return UserRoleUpdateResponseDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
//                .status(user.getStatus())
/*                .updatedAt(user.getUpdatedAt())
                .updatedBy(user.getUpdatedBy())
                .isDeleted(user.getIsDeleted())
                */
                .build();
    }
}

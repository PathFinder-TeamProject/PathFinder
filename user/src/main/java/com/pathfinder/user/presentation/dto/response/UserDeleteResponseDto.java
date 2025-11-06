package com.pathfinder.user.presentation.dto.response;

import com.pathfinder.user.domain.entity.UserEntity;
import com.pathfinder.user.domain.enums.UserRoleEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserDeleteResponseDto {

    private String username;

    private String email;

    private String name;

    private LocalDateTime deletedAt;

    private boolean isDeleted;
    public static UserDeleteResponseDto of(UserEntity user) {
        return UserDeleteResponseDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
/*                .deletedAt(user.getDeletedAt())
                .isDeleted(user.getIsDeleted())*/
                .build();
    }
}

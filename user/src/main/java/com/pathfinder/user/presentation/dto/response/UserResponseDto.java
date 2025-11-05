package com.pathfinder.user.presentation.dto.response;

import com.pathfinder.user.domain.entity.UserEntity;
import com.pathfinder.user.domain.enums.UserRoleEnum;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponseDto {

    private Long id;

    private String name;

    private String email;

    private UserRoleEnum role;

    private LocalDateTime createdAt;

    private boolean isDeleted;

}

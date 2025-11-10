package com.pathfinder.user.presentation.dto.response;

import com.pathfinder.user.domain.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public class UserUpdateResponseDto {

    private String username;

    private String email;

    private String name;

    private String modifiedBy;

    private boolean isDeleted;
    public static UserUpdateResponseDto of(UserEntity user) {
        return UserUpdateResponseDto.builder()
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .modifiedBy(user.getModifiedBy())
                .isDeleted(user.getDeletedBy()==null? false:true)
                .build();
    }
}

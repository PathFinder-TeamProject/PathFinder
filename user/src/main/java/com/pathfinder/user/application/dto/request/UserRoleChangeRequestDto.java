package com.pathfinder.user.application.dto.request;

import com.pathfinder.user.domain.enums.UserRoleEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserRoleChangeRequestDto {

    @NotNull
    private UserRoleEnum role;
}

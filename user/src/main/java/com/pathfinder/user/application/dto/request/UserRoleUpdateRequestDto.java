package com.pathfinder.user.application.dto.request;

import com.pathfinder.user.domain.enums.UserRoleEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserRoleUpdateRequestDto {

//    @NotNull
    private UserRoleEnum role;
}

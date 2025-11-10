package com.pathfinder.user.application.dto.request;
import com.pathfinder.user.domain.enums.UserStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserStatusUpdateRequestDto {
//    @NotNull
    private UserStatusEnum status;
}

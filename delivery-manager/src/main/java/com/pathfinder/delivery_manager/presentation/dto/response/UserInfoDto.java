package com.pathfinder.delivery_manager.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDto {
    private String username;
    private String name;
    private String email;
    private String organization;
    private String slackId;
    private String role;
    private String status;
}

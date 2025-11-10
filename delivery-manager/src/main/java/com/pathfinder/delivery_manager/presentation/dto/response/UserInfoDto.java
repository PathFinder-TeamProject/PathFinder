package com.pathfinder.delivery_manager.presentation.dto.response;

import lombok.*;

import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Setter
public class UserInfoDto {
    private String username;
    private String name;
    private String email;
    private String organization;
    private String slackId;
    private String role;
    private String status;
    private Long hubId;
    private Instant createdAt;
    private boolean isDeleted;
}

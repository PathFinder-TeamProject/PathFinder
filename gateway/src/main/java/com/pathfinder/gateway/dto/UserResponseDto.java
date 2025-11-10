package com.pathfinder.gateway.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public class UserResponseDto {
    private String username;

    private String email;

    private String name;

    private String organization;

    private String eventType;

    private String slackId;

    private String role;

    private Long hubId;

    private String status;

    private LocalDateTime createdAt;

    private boolean isDeleted;

}

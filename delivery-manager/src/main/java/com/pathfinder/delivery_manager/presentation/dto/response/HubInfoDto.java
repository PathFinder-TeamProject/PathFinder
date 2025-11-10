package com.pathfinder.delivery_manager.presentation.dto.response;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Setter
public class HubInfoDto {
    private Long hubId;
    private String hubName;
}
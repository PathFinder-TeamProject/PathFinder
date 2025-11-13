package com.pathfinder.delivery_manager.presentation.dto.response;

import lombok.*;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Setter
public class HubInfoDto {
    private UUID hubId;
    private String hubName;
    private String hubAddress;
    private String hubManagerUsername;
}
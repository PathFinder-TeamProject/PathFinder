package com.pathfinder.user.kafka.dto;


import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryManagerEventDto {
    private String username;
    private UUID hubId;
    private String type;         // HUB or COMPANY
    private String eventType;    // CREATED, UPDATED, DELETED
    private LocalDateTime occurredAt;
}
package com.pathfinder.order.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class OrderUpdateRequestDto {

    private int quantity;

    private String request;

    private Timestamp deadline;
}

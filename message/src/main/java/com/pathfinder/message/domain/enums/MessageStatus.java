package com.pathfinder.message.domain.enums;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

public enum MessageStatus {
    scheduled,
    sent,
    failed,
    deleted
}
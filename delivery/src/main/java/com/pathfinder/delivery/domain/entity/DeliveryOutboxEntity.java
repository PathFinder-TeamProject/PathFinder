package com.pathfinder.delivery.domain.entity;

import com.pathfinder.delivery.domain.enums.DeliveryOutboxStatus;
import com.pathfinder.delivery.domain.event.DeliveryEventDto;
import com.pathfinder.global.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_delivery_outbox")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryOutboxEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "outbox_id")
    private UUID outboxId;

    @Column(name = "aggregate_type", nullable = false, length = 50)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Lob
    @Column(name = "payload", nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private DeliveryOutboxStatus status = DeliveryOutboxStatus.PENDING;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "last_error_message")
    private String lastErrorMessage;

    @Column(name = "retry_count")
    @Builder.Default
    private int retryCount = 0;

    @Column(name = "last_attempt_at")
    private Instant lastAttemptAt;

    public void markProcessing() {
        this.status = DeliveryOutboxStatus.PROCESSING;
        this.retryCount += 1;
        this.lastAttemptAt = Instant.now();
    }

    public void markPublished() {
        this.status = DeliveryOutboxStatus.PUBLISHED;
        this.publishedAt = Instant.now();
        this.lastErrorMessage = null;
    }

    public void markFailed(String errorMessage, boolean finalFailure) {
        this.status = finalFailure ? DeliveryOutboxStatus.FAILED : DeliveryOutboxStatus.PENDING;
        this.lastErrorMessage = truncateError(errorMessage);
    }

    private String truncateError(String errorMessage) {
        if (errorMessage == null) {
            return null;
        }
        return errorMessage.length() > 500 ? errorMessage.substring(0, 500) : errorMessage;
    }
}


package com.pathfinder.message.domain.entity;

import com.pathfinder.global.infrastructure.entity.BaseEntity;
import com.pathfinder.message.domain.enums.MessageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "p_message")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "message_id", nullable = false)
    private UUID messageId;

    @Column(nullable = false, length = 4000)
    private String request;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(name = "receiver_id", nullable = false)
    private UUID receiverId;

    @Column(name = "send_at")
    private Instant sendAt;

    @Column(name = "scheduledTime", nullable = false)
    private Instant scheduledTime;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    public void delete(){
        this.status = MessageStatus.DELETED;
    }

    public void changeStatus(MessageStatus newStatus){
        this.status = newStatus;
    }
}

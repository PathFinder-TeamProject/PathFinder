package com.pathfinder.delivery_manager.domain.entity;

import com.pathfinder.delivery_manager.application.dto.request.DeliveryManagerCreateRequestDto;
import com.pathfinder.delivery_manager.application.dto.request.DeliveryManagerUpdateRequestDto;
import com.pathfinder.delivery_manager.domain.enums.DeliveryManagerTypeEnum;
import com.pathfinder.global.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_delivery_manager")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryManagerEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID deliveryManagerId;

    @Column(nullable = false, updatable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private DeliveryManagerTypeEnum type;

    @Column(nullable = false)
    private int deliveryOrder;

    @Column(nullable = false)
    private UUID hubId;

    public static DeliveryManagerEntity create(@Valid DeliveryManagerCreateRequestDto requestDto) {
        return DeliveryManagerEntity.builder()
                .username(requestDto.getUsername())
                .type(requestDto.getType())
                .deliveryOrder(requestDto.getDeliveryOrder())
                .hubId(requestDto.getHubId())
                .build();
    }
    public void setDeliveryOrder(int deliveryOrder) {
        this.deliveryOrder = deliveryOrder;
    }

    public void update(DeliveryManagerUpdateRequestDto requestDto) {
        this.type = requestDto.getType() == null ? this.type : requestDto.getType();
        this.hubId = requestDto.getHubId() == null ? this.hubId : requestDto.getHubId();
    }
}

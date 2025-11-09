package com.pathfinder.delivery_manager.domain.entity;

import com.pathfinder.delivery_manager.application.dto.request.DeliveryManagerRequestDto;
import com.pathfinder.delivery_manager.domain.enums.DeliveryManagerTypeEnum;
import com.pathfinder.global.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "p_delivery_manager")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryManagerEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryManagerId;

    @Column(nullable = false, updatable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private DeliveryManagerTypeEnum type;

    @Column(nullable = false)
    private int deliveryOrder;

    @Column(nullable = false)
    private Long hubId;


    public static DeliveryManagerEntity create(DeliveryManagerRequestDto requestDto) {
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

    public void update(DeliveryManagerRequestDto requestDto) {
        this.type = requestDto.getType() == null ? this.type : requestDto.getType();
        this.deliveryOrder = requestDto.getDeliveryOrder() == null ? this.deliveryOrder : requestDto.getDeliveryOrder().intValue();
        this.hubId = requestDto.getHubId() == null ? this.hubId : requestDto.getHubId();
    }
}

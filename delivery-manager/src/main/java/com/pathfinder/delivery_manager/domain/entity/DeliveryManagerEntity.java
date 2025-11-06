package com.pathfinder.delivery_manager.domain.entity;

import com.pathfinder.delivery_manager.domain.enums.DeliveryManagerTypeEnum;
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
public class DeliveryManagerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryManagerId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private DeliveryManagerTypeEnum type;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private int deliveryOrder;

    @Column(nullable = false)
    private Long hubId;
}

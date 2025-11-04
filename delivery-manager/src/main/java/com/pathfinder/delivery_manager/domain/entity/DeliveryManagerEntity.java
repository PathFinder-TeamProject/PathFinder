package com.pathfinder.delivery_manager.domain.entity;

import jakarta.persistence.*;
import org.springframework.security.crypto.password.PasswordEncoder;
@Entity
@Table(name = "p_delivery_manager")
public class DeliveryManagerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}

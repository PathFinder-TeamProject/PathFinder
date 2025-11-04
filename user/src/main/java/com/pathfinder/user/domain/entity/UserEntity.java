package com.pathfinder.user.domain.entity;


import jakarta.persistence.*;
import org.springframework.security.crypto.password.PasswordEncoder;
@Entity
@Table(name = "p_users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}

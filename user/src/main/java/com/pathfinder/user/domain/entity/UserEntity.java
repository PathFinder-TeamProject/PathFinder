package com.pathfinder.user.domain.entity;

import com.pathfinder.global.infrastructure.entity.BaseEntity;
import com.pathfinder.user.application.dto.request.SignupRequestDto;
import com.pathfinder.user.application.dto.request.UserUpdateRequestDto;
import com.pathfinder.user.domain.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "p_users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity extends BaseEntity {

    @Id
    private String username;

    @Column
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String organization;

    @Column(nullable = false)
    private String slackId;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @Builder.Default
    private UserStatusEnum status = UserStatusEnum.PENDING;

    public static UserEntity create(SignupRequestDto requestDto, String encodingPassword) {
        return UserEntity.builder()
                .email(requestDto.getEmail())
                .username(requestDto.getUsername())
                .slackId(requestDto.getSlackId())
                .name(requestDto.getName())
                .password(encodingPassword)
                .organization(requestDto.getOrganization())
                .role(requestDto.getRole())
                .status(UserStatusEnum.PENDING)
                .build();
    }

    public void update(UserUpdateRequestDto requestDto, PasswordEncoder passwordEncoder) {
        this.name = requestDto.getName() == null ? this.name : requestDto.getName();
        this.password = requestDto.getNewPassword() == null ? this.password : passwordEncoder.encode(requestDto.getNewPassword());
    }

    public void updateStatus(UserStatusEnum status) {
        this.status = status;
    }
    public void updateRole(UserRoleEnum role) {
        this.role = role;
    }
}

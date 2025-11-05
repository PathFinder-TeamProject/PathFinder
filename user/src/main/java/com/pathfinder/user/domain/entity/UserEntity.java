package com.pathfinder.user.domain.entity;

import com.pathfinder.user.application.dto.request.SignupRequestDto;
import com.pathfinder.user.application.dto.request.UserUpdateRequestDto;
import com.pathfinder.user.domain.enums.UserRoleEnum;
import com.pathfinder.user.domain.enums.UserStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "p_users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

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

    @Column
    private String slackId;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserStatusEnum status = UserStatusEnum.PENDING;

    public static UserEntity create(SignupRequestDto requestDto, String encodingPassword) {
        return UserEntity.builder()
                .email(requestDto.getEmail())
                .name(requestDto.getName())
                .password(encodingPassword)
                .organization(requestDto.getOrganization())
                .role(requestDto.getRole())
                .build();
    }

    public void update(UserUpdateRequestDto requestDto, PasswordEncoder passwordEncoder) {
        this.name = requestDto.getName() == null ? this.name : requestDto.getName();
        this.password = requestDto.getNewPassword() == null ? this.password : passwordEncoder.encode(requestDto.getNewPassword());
    }

    public void changeRole(UserRoleEnum role) {
        this.role = role;
    }
}

package com.pathfinder.user.domain.repository;

import com.pathfinder.user.domain.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepository {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findBySlackId(String slackId);

    UserEntity save(UserEntity user);

    Page<UserEntity> findAll(Pageable pageable);

    Page<UserEntity> findByOrganization(String organization, Pageable pageable);

    Optional<UserEntity> findByUsernameAndDeletedAtIsNull(String username);

//    Optional<UserEntity> findByUsernameAndIsDeletedFalse(String username);
}

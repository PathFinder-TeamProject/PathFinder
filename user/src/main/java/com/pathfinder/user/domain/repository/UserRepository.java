package com.pathfinder.user.domain.repository;

import com.pathfinder.user.domain.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepository {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    UserEntity save(UserEntity user);

    Page<UserEntity> findAll(Pageable pageable);

//    Optional<UserEntity> findByEmailAndIsDeletedFalse(String email);
}

package com.pathfinder.user.infrastructure.repository;

import com.pathfinder.user.domain.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


import com.pathfinder.user.domain.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findBySlackId(String slackId);

    Page<UserEntity> findAll(Pageable pageable);

    Page<UserEntity> findByOrganization(String organization, Pageable pageable);

//    Optional<UserEntity> findByEmailAndIsDeletedFalse(String email);
}
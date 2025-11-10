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
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, String> {

    @Query("SELECT u FROM UserEntity u WHERE u.username = :username AND u.deletedAt IS NULL")
    Optional<UserEntity> findByUsername(String username);

    @Query("SELECT u FROM UserEntity u WHERE u.email = :email AND u.deletedAt IS NULL")
    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.slackId = :slackId AND u.deletedAt IS NULL")
    Optional<UserEntity> findBySlackId(String slackId);

    @Query("SELECT u FROM UserEntity u WHERE u.deletedAt IS NULL")
    Page<UserEntity> findAll(Pageable pageable);

    @Query("SELECT u FROM UserEntity u WHERE u.organization = :organization AND u.deletedAt IS NULL")
    Page<UserEntity> findByOrganization(String organization, Pageable pageable);

//    Optional<UserEntity> findByEmailAndIsDeletedFalse(String email);
}
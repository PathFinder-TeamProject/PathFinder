package com.pathfinder.user.infrastructure.repository;


import com.pathfinder.user.domain.entity.UserEntity;
import com.pathfinder.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@RequiredArgsConstructor
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<UserEntity> findByUsername(String username) { return userJpaRepository.findByUsername(username); }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }

    @Override
    public UserEntity save(UserEntity user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Page<UserEntity> findAll(Pageable pageable) {
        return userJpaRepository.findAll(pageable);
    }

/*    @Override
    public Optional<UserEntity> findByEmailAndIsDeletedFalse(String email) {
        return userJpaRepository.findByEmailAndIsDeletedFalse(email);
    }*/
}
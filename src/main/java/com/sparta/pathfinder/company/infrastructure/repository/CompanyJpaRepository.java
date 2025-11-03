package com.sparta.pathfinder.company.infrastructure.repository;

import com.sparta.pathfinder.company.domain.entity.CompanyEntity;
import com.sparta.pathfinder.user.domain.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyJpaRepository extends JpaRepository<CompanyEntity, Long> {

}

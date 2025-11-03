package com.sparta.pathfinder.product.infrastructure.repository;

import com.sparta.pathfinder.product.domain.entity.ProductEntity;
import com.sparta.pathfinder.user.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

}

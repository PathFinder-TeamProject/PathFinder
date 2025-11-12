package com.pathfinder.product.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pathfinder.product.domain.entity.Product;

public interface ProductRepository extends JpaRepository<Product, UUID> {

	@Query("""
    SELECT c FROM Product c
    WHERE (:keyword = '' 
           OR LOWER(c.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR CAST(c.companyId AS string) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR CAST(c.price AS string) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR CAST(c.hubId AS string) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
    AND c.deletedAt IS NULL
    """)
	Page<Product> findByKeyword(String keyword, Pageable pageable);

}

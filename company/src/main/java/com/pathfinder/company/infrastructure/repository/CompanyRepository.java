package com.pathfinder.company.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pathfinder.company.domain.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, UUID>{

	Optional<Company> findByBusinessNo(String businessNo);

	boolean existsByBusinessNo(String businessNo);

	@Query("""
        SELECT c FROM Company c
        WHERE (:keyword = '' 
               OR LOWER(c.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(c.businessNo) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(c.address) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(c.manager) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        AND c.deletedAt IS NULL
        """)
	Page<Company> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}

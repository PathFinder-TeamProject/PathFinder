package com.pathfinder.product.domain.entity;

import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.Where;

import com.pathfinder.global.infrastructure.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_product")
@Builder @Getter
@NoArgsConstructor @AllArgsConstructor
@SQLDelete(sql = "UPDATE p_product SET deleted_at = NOW(), deleted_by = ? WHERE product_id = ?")
@Where(clause = "deleted_at IS NULL")

public class Product extends BaseEntity {

	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "product_id")
	private UUID productId;

	@Column(name = "product_name", nullable = false)
	private String productName;

	//업체 ID
	@Column(name = "company_id", nullable = false)
	private UUID companyId;

	//상품 관리 허브 ID
	@Column(name = "hub_id", nullable = false)
	private UUID hubId;

	@Column(nullable = false)
	private Double price;

	@Column(nullable = false)
	private Integer stock;


public void  updateProduct(String productName, Double price, Integer stock)
{
	this.productName = productName;
	this.price = price;
	this.stock = stock;
}

}

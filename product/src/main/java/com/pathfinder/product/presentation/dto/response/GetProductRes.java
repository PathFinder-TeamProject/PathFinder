package com.pathfinder.product.presentation.dto.response;

import java.util.UUID;

import com.pathfinder.product.domain.entity.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetProductRes {

	private UUID  productId;
	private String productName;
	private UUID hubId;
	private UUID companyId;
	private Integer stock;
	private Double price;

	public static GetProductRes fromEntity(Product product) {
		return GetProductRes.builder()
			.productId(product.getProductId())
			.companyId(product.getCompanyId())
			.hubId(product.getHubId())
			.productName(product.getProductName())
			.price(product.getPrice())
			.stock(product.getStock())
			.build();
	}
}

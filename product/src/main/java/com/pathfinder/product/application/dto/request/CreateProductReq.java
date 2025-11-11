package com.pathfinder.product.application.dto.request;

import java.util.UUID;

import com.pathfinder.product.domain.entity.Product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateProductReq {

	@NotBlank(message = "상품명은 필수입력입니다.")
	private String productName;

	@NotNull(message = "허브ID는 필수입니다..")
	private UUID hubId;

	@NotNull(message = "기업ID는 필수입니다..")
	private UUID companyId;

	@NotNull(message = "상품 가격은 필수입력입니다.")
	private Double price;

	@NotNull(message = "수량은 필수 입력입니다.")
	private Integer stock;

	public Product toEntity() {
		return Product.builder()
			.productName(productName)
			.price(price)
			.stock(stock)
			.companyId(companyId)
			.hubId(hubId)
			.build();
	}
}

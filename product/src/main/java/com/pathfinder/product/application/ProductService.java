package com.pathfinder.product.application;

import java.time.Instant;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pathfinder.product.application.dto.request.CreateProductReq;
import com.pathfinder.product.application.dto.request.UpdateProductReq;
import com.pathfinder.product.application.exception.ProductErrorCode;
import com.pathfinder.product.application.exception.ProductException;
import com.pathfinder.product.domain.entity.Product;
import com.pathfinder.product.infrastructure.repository.ProductRepository;
import com.pathfinder.product.presentation.dto.response.GetProductRes;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;

    @Transactional
    public void decreaseStock(UUID productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));
        product.updateProduct(product.getProductName(), product.getPrice(), product.getStock() - quantity);
    };

    /* ============= CREATE ============= */
	@Transactional
	@CacheEvict(value = {"product", "productList"}, allEntries = true)
	public GetProductRes createProduct(CreateProductReq req) {
		// 허브 / 업체 / 권한 검증 로직
		Product product = productRepository.save(req.toEntity());
		return GetProductRes.fromEntity(product);
	}

	/* ============= UPDATE ============= */
	@Transactional
	@CachePut(value = "product", key = "#id")
	public GetProductRes updateProduct(UUID id, UpdateProductReq req) {
		Product product = productRepository.findById(id)
			.orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

		product.updateProduct(req.getProductName(), req.getPrice(), req.getStock());
		return GetProductRes.fromEntity(product);
	}

	/* ============= DELETE ============= */
	@Transactional
	@CacheEvict(value = "product", key = "#id")
	public void deleteProduct(UUID id) {
		Product product = productRepository.findById(id)
			.orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));
		product.softDelete(Instant.now(), "System");
	}

	/* ============= GET BY ID ============= */
	@Transactional(readOnly = true)
	@Cacheable(value = "product", key = "#id")
	public GetProductRes getProductById(UUID id) {
		Product product = productRepository.findById(id)
			.orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));
		return GetProductRes.fromEntity(product);
	}

	/* ============= SEARCH (LIST) ============= */
	@Transactional(readOnly = true)
	@Cacheable(
		value = "productList",
		key = "T(String).format('%s_%s_%s_%s_%s', #keyword, #sortBy, #sortDir, #page, #size)"
	)
	public Page<GetProductRes> getAllProducts(String keyword, String sortBy, String sortDir, int page, int size) {
		Sort sort = Sort.by(sortBy.equals("modifiedAt") ? "modifiedAt" : "createdAt");
		if (sortDir.equalsIgnoreCase("desc")) sort = sort.descending();
		Pageable pageable = PageRequest.of(page, size, sort);

		Page<Product> productPage = productRepository.findByKeyword(keyword, pageable);
		return productPage.map(GetProductRes::fromEntity);
	}
}

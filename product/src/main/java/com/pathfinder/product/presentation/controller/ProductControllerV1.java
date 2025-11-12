package com.pathfinder.product.presentation.controller;

import java.util.UUID;

import com.pathfinder.product.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pathfinder.product.application.ProductService;
import com.pathfinder.product.application.dto.request.CreateProductReq;
import com.pathfinder.product.application.dto.request.UpdateProductReq;
import com.pathfinder.product.presentation.dto.response.ApiResponse;
import com.pathfinder.product.presentation.dto.response.GetProductRes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductControllerV1 {

	private final ProductService productService;

	private static final int[] ALLOWED_PAGE_SIZES = {10, 30, 50};

	@PostMapping
	public ResponseEntity<ApiResponse<GetProductRes>> createProduct(
		@Valid  @RequestBody CreateProductReq req,
		BindingResult bindingResult
		//@AuthenticationPrincipal
	){
		if(bindingResult.hasErrors()){
			String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
			return ResponseEntity.badRequest().body(ApiResponse.fail("400 ERROR",message));

		}
		GetProductRes getProductRes = productService.createProduct(req);
		return ResponseEntity.ok(ApiResponse.success(getProductRes));
	}

	@PutMapping("/{productId}")
	public ResponseEntity<ApiResponse<GetProductRes>> updateProduct(
		@PathVariable UUID productId,
		@Valid @RequestBody UpdateProductReq req,
		BindingResult bindingResult
		//@AuthenticationPrincipal
	){
		if(bindingResult.hasErrors()){
			String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
			return ResponseEntity.badRequest().body(ApiResponse.fail("400 ERROR",message));
		}

		GetProductRes getProductRes = productService.updateProduct(productId, req);
		return ResponseEntity.ok(ApiResponse.success(getProductRes));
	}

	@DeleteMapping("/{productId}")
	public ResponseEntity<ApiResponse<Void>> deleteProduct(
		@PathVariable UUID productId
		//@AuthenticationPrincipal
	){
		productService.deleteProduct(productId);
		return  ResponseEntity.ok(ApiResponse.noContent());
	}

	@GetMapping("/{productId}")
	public ResponseEntity<ApiResponse<GetProductRes>> getProduct(
		@PathVariable UUID productId
	){
		return ResponseEntity.ok(ApiResponse.success(productService.getProductById(productId)));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<Page<GetProductRes>>> getAllProducts(
		@RequestParam(defaultValue = "") String keyword,
		@RequestParam(defaultValue = "createdAt") String sortBy,
		@RequestParam(defaultValue = "desc") String sortDir,
		Pageable pageable
	){
		int size = sanitizePageSize(pageable.getPageSize());
		Page<GetProductRes> result = productService.getAllProducts(keyword, sortBy, sortDir,
			pageable.getPageNumber(), size);
		return ResponseEntity.ok(ApiResponse.success(result));
	}

	private int sanitizePageSize(int size) {
		for (int allowed : ALLOWED_PAGE_SIZES) {
			if (size == allowed)
				return size;
		}
		return 10; // 기본값
	}

    @PutMapping("/{productId}/order")
    public ResponseEntity<ApiResponse<Void>> decreaseStock(@PathVariable UUID productId, @RequestParam int quantity){
        productService.decreaseStock(productId, quantity);
        return ResponseEntity.ok(ApiResponse.successMessage("200","수량 차감 완료"));
    }
}

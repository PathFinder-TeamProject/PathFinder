package com.pathfinder.product.presentation.controller;

import java.util.UUID;

import com.pathfinder.product.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.pathfinder.product.application.ProductService;
import com.pathfinder.product.application.dto.request.CreateProductReq;
import com.pathfinder.product.application.dto.request.UpdateProductReq;
import com.pathfinder.product.presentation.dto.response.ApiResponse;
import com.pathfinder.product.presentation.dto.response.GetProductRes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "상품 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductControllerV1 {

    private final ProductService productService;

    private static final int[] ALLOWED_PAGE_SIZES = {10, 30, 50};

    @Operation(summary = "상품 생성", description = "상품을 신규 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<GetProductRes>> createProduct(
            @Valid @RequestBody CreateProductReq req,
            BindingResult bindingResult
    ){
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(ApiResponse.fail("400 ERROR", message));
        }

        GetProductRes getProductRes = productService.createProduct(req);
        return ResponseEntity.ok(ApiResponse.success(getProductRes));
    }

    @Operation(summary = "상품 수정", description = "상품 정보를 수정합니다.")
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<GetProductRes>> updateProduct(
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductReq req,
            BindingResult bindingResult
    ){
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(ApiResponse.fail("400 ERROR", message));
        }

        GetProductRes getProductRes = productService.updateProduct(productId, req);
        return ResponseEntity.ok(ApiResponse.success(getProductRes));
    }

    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다.")
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable UUID productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

    @Operation(summary = "상품 상세 조회", description = "특정 상품의 상세 정보를 조회합니다.")
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<GetProductRes>> getProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(ApiResponse.success(productService.getProductById(productId)));
    }

    @Operation(
            summary = "상품 목록 조회",
            description = "상품을 검색어(keyword), 정렬(sortBy, sortDir), 페이지(page, size) 조건으로 조회합니다."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<GetProductRes>>> getAllProducts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Pageable pageable
    ){
        int size = sanitizePageSize(pageable.getPageSize());
        Page<GetProductRes> result = productService.getAllProducts(
                keyword, sortBy, sortDir, pageable.getPageNumber(), size
        );

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    private int sanitizePageSize(int size) {
        for (int allowed : ALLOWED_PAGE_SIZES) {
            if (size == allowed) return size;
        }
        return 10;
    }

    @Operation(summary = "상품 재고 차감", description = "주문 시 사용되는 API로, 상품 재고에서 특정 수량을 차감합니다.")
    @PutMapping("/{productId}/order")
    public ResponseEntity<ApiResponse<Void>> decreaseStock(
            @PathVariable UUID productId,
            @RequestParam int quantity
    ){
        productService.decreaseStock(productId, quantity);
        return ResponseEntity.ok(ApiResponse.successMessage("200", "수량 차감 완료"));
    }
}

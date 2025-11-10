package com.pathfinder.company.presentation.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

import com.pathfinder.company.application.CompanyService;
import com.pathfinder.company.application.dto.request.CreateCompanyReq;
import com.pathfinder.company.application.dto.request.UpdateCompanyReq;
import com.pathfinder.company.presentation.dto.response.ApiResponse;
import com.pathfinder.company.presentation.dto.response.GetCompanyRes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/companies")
public class CompanyControllerV1 {

	private final CompanyService companyService;


	private static final int[] ALLOWED_PAGE_SIZES = {10, 30, 50};

	@PostMapping
	public ResponseEntity<ApiResponse<GetCompanyRes>> CreateCompany(
		@Valid @RequestBody CreateCompanyReq createCompanyReq,
		BindingResult bindingResult
		//@AuthenticationPrincipal
	){
		if(bindingResult.hasErrors()){
			String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
			return ResponseEntity.badRequest().body(ApiResponse.fail("400 ERROR",message));
		}

		GetCompanyRes getCompanyRes = companyService.createCompany(createCompanyReq);
		return ResponseEntity.ok(ApiResponse.success(getCompanyRes));
	}

	@PutMapping("/{companyId}")
	public ResponseEntity<ApiResponse<GetCompanyRes>> UpdateCompany(
		@PathVariable UUID companyId,
		@Valid @RequestBody UpdateCompanyReq req,
		BindingResult bindingResult
		//@AuthenticationPrincipal
	){
		if(bindingResult.hasErrors()){
			String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
			return ResponseEntity.badRequest().body(ApiResponse.fail("400 ERROR",message));
		}

		GetCompanyRes getCompanyRes = companyService.updateCompany(companyId, req);
		return ResponseEntity.ok(ApiResponse.success(getCompanyRes));
	}

	@DeleteMapping("/{companyId}")
	public ResponseEntity<ApiResponse<Void>> DeleteCompany(
		@PathVariable UUID companyId
		//@AuthenticationPrincipal
	){
		companyService.deleteCompany(companyId, "system");
		return ResponseEntity.ok(ApiResponse.noContent());
	}

	@GetMapping
	public ResponseEntity<ApiResponse<Page<GetCompanyRes>>> getAllCompany(
		@RequestParam(defaultValue = "") String keyword,
		@RequestParam(defaultValue = "createdAt") String sortBy,
		@RequestParam(defaultValue = "desc") String sortDir,
		Pageable pageable
	) {
		int sanitizedSize = sanitizePageSize(pageable.getPageSize());
		Page<GetCompanyRes> result = companyService.getAllCompanies(keyword, sortBy, sortDir,
			pageable.getPageNumber(), sanitizedSize);
		return ResponseEntity.ok(ApiResponse.success(result));
	}


	@GetMapping("/{companyId}")
	public ResponseEntity<ApiResponse<GetCompanyRes>> getCompany(
		@PathVariable UUID companyId
	){
		return ResponseEntity.ok(ApiResponse.success(companyService.getCompanyById(companyId)));
	}


	private int sanitizePageSize(int size) {
		for (int allowed : ALLOWED_PAGE_SIZES) {
			if (size == allowed)
				return size;
		}
		return 10; // 기본값
	}

}

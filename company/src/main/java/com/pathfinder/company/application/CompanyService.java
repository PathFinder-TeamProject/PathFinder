package com.pathfinder.company.application;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pathfinder.company.application.dto.request.CreateCompanyReq;
import com.pathfinder.company.application.dto.request.UpdateCompanyReq;
import com.pathfinder.company.application.exception.CompanyErrorCode;
import com.pathfinder.company.application.exception.CompanyException;
import com.pathfinder.company.domain.entity.Company;
import com.pathfinder.company.infrastructure.repository.CompanyRepository;
import com.pathfinder.company.presentation.dto.response.GetCompanyRes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {

	private final CompanyRepository companyRepository;

	@Transactional
	@CacheEvict(value = {"companyList"}, allEntries = true)
	public GetCompanyRes createCompany(CreateCompanyReq req) {
		if(companyRepository.existsByBusinessNo(req.getBusinessNo())) {
			throw new CompanyException(CompanyErrorCode.DUPLICATE_BUSINESS_NO);
		}

		Company company =  companyRepository.save(req.toEntity());
		return GetCompanyRes.fromEntity(company);
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "companyList", key = "{#keyword, #sortBy, #sortDir, #page, #size}")
	public Page<GetCompanyRes> getAllCompanies(String keyword, String sortBy, String sortDir, int page, int size) {
		Sort sort = Sort.by(sortBy.equals("modifiedAt") ? "modifiedAt" : "createdAt");
		if (sortDir.equalsIgnoreCase("desc")) sort = sort.descending();

		Pageable pageable = PageRequest.of(page, size, sort);
		Page<Company> companyPage = companyRepository.findByKeyword(keyword, pageable);
		return companyPage.map(GetCompanyRes::fromEntity);
	}

	@Transactional(readOnly = true)
	@Cacheable(value = "company", key = "#companyId")
	public GetCompanyRes getCompanyById(UUID id) {
		Company company = companyRepository.findById(id)
			.orElseThrow(() -> new CompanyException(CompanyErrorCode.COMPANY_NOT_FOUND));
		return GetCompanyRes.fromEntity(company);
	}

	@Transactional
	@CacheEvict(value = {"company", "companyList"}, allEntries = true)
	public GetCompanyRes updateCompany(UUID id, UpdateCompanyReq req) {
		Company company = companyRepository.findById(id)
			.orElseThrow(() -> new CompanyException(CompanyErrorCode.COMPANY_NOT_FOUND));

		company.update(
			req.getCompanyName(),
			req.getAddress(),
			req.getManager(),
			req.getBusinessNo()
		);

		return GetCompanyRes.fromEntity(companyRepository.save(company));
	}

	@Transactional
	@CacheEvict(value = {"company", "companyList"}, allEntries = true)
	public void deleteCompany(UUID id, String username) {
		Company company = companyRepository.findById(id)
			.orElseThrow(() -> new CompanyException(CompanyErrorCode.COMPANY_NOT_FOUND));

		company.softDelete(Instant.now(),"system");
	}


	/*========================권한체크==================*/
}

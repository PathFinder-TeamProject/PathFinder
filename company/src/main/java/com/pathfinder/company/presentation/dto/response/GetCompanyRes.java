package com.pathfinder.company.presentation.dto.response;

import java.util.UUID;

import com.pathfinder.company.domain.entity.Company;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetCompanyRes{

	private UUID companyId;
	private String companyName;
	private String businessNo;
	private String address;
	private String manager;
	private UUID hubId;
	private boolean producer;
	private boolean receiver;

	public static GetCompanyRes fromEntity(Company company) {
		return GetCompanyRes.builder()
			.companyId(company.getCompanyId())
			.companyName(company.getCompanyName())
			.businessNo(company.getBusinessNo())
			.address(company.getAddress())
			.manager(company.getManager())
			.hubId(company.getHubId())
			.producer(company.isProducer())
			.receiver(company.isReceiver())
			.build();
	}
}

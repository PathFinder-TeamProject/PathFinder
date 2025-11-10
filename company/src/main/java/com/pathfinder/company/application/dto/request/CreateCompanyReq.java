package com.pathfinder.company.application.dto.request;

import java.util.UUID;

import com.pathfinder.company.domain.entity.Company;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateCompanyReq {

	@NotBlank(message = "회사명은 필수 입력값입니다.")
	private String companyName;

	@NotBlank(message = "사업자 등록번호는 필수입니다.")
	private String businessNo;

	@NotBlank(message = "주소는 필수 입력값입니다.")
	private String address;

	@NotBlank(message = "담당자 ID는 필수입니다.")
	private String manager;

	@NotNull(message = "허브 ID는 필수입니다.")
	private UUID hubId;

	private boolean producer;
	private boolean receiver;

	public Company toEntity() {
		return Company.builder()
			.companyName(companyName)
			.businessNo(businessNo)
			.address(address)
			.manager(manager)
			.hubId(hubId)
			.producer(producer)
			.receiver(receiver)
			.build();
	}
}

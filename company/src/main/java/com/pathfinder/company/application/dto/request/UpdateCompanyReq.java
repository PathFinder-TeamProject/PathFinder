package com.pathfinder.company.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateCompanyReq {

	@NotBlank(message = "회사명은 필수 입력값입니다.")
	private String companyName;

	@NotBlank(message = "주소는 필수 입력값입니다.")
	private String address;

	@NotBlank(message = "담당자 ID는 필수 입력값입니다.")
	private String manager;

	@NotBlank(message = "사업자 번호는 필수 입력값입니다.")
	private String businessNo;
}

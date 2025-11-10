package com.pathfinder.company.application.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompanyErrorCode {

	COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회사입니다."),
	DUPLICATE_BUSINESS_NO(HttpStatus.CONFLICT, "이미 등록된 사업자 번호입니다."),
	INVALID_HUB_ID(HttpStatus.BAD_REQUEST, "허브 ID가 유효하지 않습니다."),
	INVALID_INPUT(HttpStatus.BAD_REQUEST, "요청 데이터가 유효하지 않습니다.");

	private final HttpStatus status;
	private final String message;
}

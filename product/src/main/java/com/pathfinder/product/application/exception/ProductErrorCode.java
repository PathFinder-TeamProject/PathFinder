package com.pathfinder.product.application.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode {

	COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회사입니다."),
	HUB_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 허브입니다."),
	INVALID_INPUT(HttpStatus.BAD_REQUEST, "요청 데이터가 유효하지 않습니다."),
	PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다.");

	private final HttpStatus status;
	private final String message;
}

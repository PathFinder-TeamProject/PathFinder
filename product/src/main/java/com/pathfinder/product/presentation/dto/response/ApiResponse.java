package com.pathfinder.product.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;

/**
 * MSA 전역 공통 API 응답 DTO
 * - null 필드는 출력되지 않음
 * - code, message, data 순서 보장
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"code", "message", "data"})
public class ApiResponse<T> {

	/** 비즈니스 에러 코드 (성공 시 null) */
	private final String code;

	/** 사용자 메시지 (에러 또는 성공 메세지) */
	private final String message;

	/** 응답 본문 데이터 */
	private final T data;


	private ApiResponse(String code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	/**  성공 응답 (데이터 포함) */
	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(null, null, data);
	}

	/**  성공 응답 (데이터 없이 메시지만) */
	public static <T> ApiResponse<T> successMessage(String code, String message) {
		return new ApiResponse<>(code, message, null);
	}

	/**  본문 없는 성공 응답 (BaseErrorCode 기반) */
	public static <T> ApiResponse<T> noContent() {
		return new ApiResponse<>(null, null, null);
	}

	/**  응답 데이터가 없는 실패 응답 */
	public static <T> ApiResponse<T> fail(String code, String message) {
		return new ApiResponse<>(code,message,null);
	}

	/**   실패 응답 */
	public static <T> ApiResponse<T> fail(String code, String message, T data) {
		return new ApiResponse<>(code, message, data);
	}
}

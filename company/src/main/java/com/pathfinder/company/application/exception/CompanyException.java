package com.pathfinder.company.application.exception;

import com.pathfinder.company.application.exception.CompanyErrorCode;

import lombok.Getter;

@Getter
public class CompanyException extends RuntimeException {

	private final CompanyErrorCode errorCode;

	public CompanyException(CompanyErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}

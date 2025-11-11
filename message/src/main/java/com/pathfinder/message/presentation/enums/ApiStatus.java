package com.pathfinder.message.presentation.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiStatus {

    OK(HttpStatus.OK, "요청 성공"),
    CREATED(HttpStatus.CREATED, "생성 성공"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "요청값이 올바르지 않음"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한 없음"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "주문 ID 존재하지 않음"),
    CONFLICT(HttpStatus.CONFLICT, "이미 취소된 주문"),
    UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY, "재고 부족 또는 상태 변경 불가"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류");

    private final HttpStatus httpStatus;
    private final String message;

    ApiStatus(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public int getCode() {
        return httpStatus.value();
    }
}


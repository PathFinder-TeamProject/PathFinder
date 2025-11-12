package com.pathfinder.delivery_manager.application.excpetion;

import com.pathfinder.global.presentation.error.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DeliveryManagerErrorCode implements BaseErrorCode {
    // USER 관련 에러
    DUPLICATE_DELIVERY_MANAGER(HttpStatus.CONFLICT, "DELIVERY-MANAGER-400-01", "중복된 %s입니다."),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "DELIVERY-MANAGER-401-01", "권한이 없는 유저입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "DELIVERY-MANAGER-404-01", "해당 유저를 찾을 수 없습니다."),
    MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "DELIVERY-MANAGER-400-02", "(%s)이 누락되었습니다."),
    TOO_MANY_DELIVERY_MANAGERS(HttpStatus.BAD_REQUEST, "DELIVERY-MANAGER-400-03", "허브당 최대 배송 담당자 수를 초과했습니다.");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public String getFormattedMessage(Object... args) {
        return String.format(message, args);
    }
}

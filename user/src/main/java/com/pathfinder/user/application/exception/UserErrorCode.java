package com.pathfinder.user.application.exception;

import com.pathfinder.global.presentation.error.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
    // USER 관련 에러
    DUPLICATE_USER(HttpStatus.CONFLICT, "USER-400-01", "중복된 %s입니다."),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "USER-401-01", "권한이 없는 유저입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.UNAUTHORIZED, "USER-401-02", "패스워드가 일치하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "USER-401-03", "유효하지 않은 토큰입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-404-01", "해당 유저를 찾을 수 없습니다."),
    NOT_APPROVED_USER(HttpStatus.FORBIDDEN, "USER-403-01", "활성화된 유저가 아닙니다."),
    MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "USER-400-02", "(%s)이 누락되었습니다.");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public String getFormattedMessage(Object... args) {
        return String.format(message, args);
    }
}

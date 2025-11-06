package com.pathfinder.user.application.excpetion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // USER 관련 에러
    DUPLICATE_USER(HttpStatus.CONFLICT, "중복 된 유저입니다."),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "권한이 없는 유저입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.UNAUTHORIZED, "패스워드가 일치하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}

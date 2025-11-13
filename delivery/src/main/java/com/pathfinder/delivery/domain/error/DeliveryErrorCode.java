package com.pathfinder.delivery.domain.error;

import com.pathfinder.global.presentation.error.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DeliveryErrorCode implements BaseErrorCode {

    DELIVERY_NOT_FOUND(HttpStatus.NOT_FOUND, "DELIVERY-404-01", "배달 정보를 찾을 수 없습니다."),
    DELIVERY_ALREADY_EXISTS(HttpStatus.CONFLICT, "DELIVERY-409-01", "이미 존재하는 배달입니다."),
    DELIVERY_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "DELIVERY-400-01", "이미 완료된 배달입니다."),
    DELIVERY_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "DELIVERY-400-02", "이미 취소된 배달입니다."),
    INVALID_DELIVERY_STATUS(HttpStatus.BAD_REQUEST, "DELIVERY-400-03", "유효하지 않은 배달 상태입니다."),
    
    ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "ROUTE-404-01", "배달 경로를 찾을 수 없습니다."),
    ROUTE_CALCULATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "ROUTE-500-01", "경로 계산에 실패했습니다."),
    INVALID_ROUTE_SEQUENCE(HttpStatus.BAD_REQUEST, "ROUTE-400-01", "잘못된 경로 순서입니다."),
    
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "EXTERNAL-404-01", "주문 정보를 찾을 수 없습니다."),
    HUB_NOT_FOUND(HttpStatus.NOT_FOUND, "EXTERNAL-404-02", "허브 정보를 찾을 수 없습니다."),
    DELIVERY_MANAGER_NOT_FOUND(HttpStatus.NOT_FOUND, "EXTERNAL-404-03", "배송 담당자를 찾을 수 없습니다."),
    DELIVERY_MANAGER_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "EXTERNAL-400-01", "배송 담당자가 배정 가능한 상태가 아닙니다."),
    HUB_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "EXTERNAL-503-01", "허브 서비스에 접근할 수 없습니다."),
    ORDER_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "EXTERNAL-503-02", "주문 서비스에 접근할 수 없습니다."),
    DELIVERY_MANAGER_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "EXTERNAL-503-03", "배송 담당자 서비스에 접근할 수 없습니다."),

    OUTBOX_SERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "OUTBOX-500-01", "이벤트 직렬화에 실패했습니다."),
    OUTBOX_PUBLISH_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "OUTBOX-500-02", "아웃박스 이벤트 발행에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}


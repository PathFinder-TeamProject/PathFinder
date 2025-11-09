package com.hub_service.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseDto<T> {
    private String status;  
    private String message;
    private T data;

    public static <T> ResponseDto<T> success(String message, T data) {
        return new ResponseDto<>("SUCCESS", message, data);
    }


    public static <T> ResponseDto<T> fail(String message) {
        return new ResponseDto<>("FAIL", message, null);
    }
}

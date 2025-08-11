package com.daon.be.global.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private int statusCode;
    private String message;
    private T data;
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Success", data);
    }
    
    public static <T> ApiResponse<T> error(int statusCode, String message) {
        return new ApiResponse<>(statusCode, message, null);
    }
}

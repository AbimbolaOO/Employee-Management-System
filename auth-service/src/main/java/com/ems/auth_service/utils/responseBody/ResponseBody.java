package com.ems.auth_service.utils.responseBody;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@Data
@AllArgsConstructor
public class ResponseBody<T> {
    private final boolean success;
    private String message;
    private T data;

    public ResponseBody(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static <T> ResponseBody <T> success(String message) {
        return new ResponseBody<>(true, message);
    }

    public static <T> ResponseBody<T> success(String message, T data) {
        return new ResponseBody<>(true, message, data);
    }

    public static <T> ResponseBody<Optional<T>> success(String message, Optional<T> data) {
        return new ResponseBody<>(true, message, data);
    }
}

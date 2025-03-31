package com.ems.auth_service.utils.exceptions;

import lombok.Getter;

@Getter
public class UniqueConstraintViolationException extends RuntimeException {
    public UniqueConstraintViolationException(String message) {
        super(message);
    }
}
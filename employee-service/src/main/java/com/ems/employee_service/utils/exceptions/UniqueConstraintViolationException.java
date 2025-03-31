package com.ems.employee_service.utils.exceptions;

import lombok.Getter;

@Getter
public class UniqueConstraintViolationException extends RuntimeException {
    public UniqueConstraintViolationException(String message) {
        super(message);
    }
}
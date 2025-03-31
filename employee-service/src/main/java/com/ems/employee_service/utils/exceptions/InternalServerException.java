package com.ems.employee_service.utils.exceptions;


public class InternalServerException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Internal server error";

    public InternalServerException() {
        super(DEFAULT_MESSAGE);
    }

    public InternalServerException(String message) {
        super(message);
    }

    public InternalServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
package com.ems.auth_service.utils.exceptions;

public class UnprocessableEntityException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Unprocessable Entity";

    public UnprocessableEntityException() {
        super(DEFAULT_MESSAGE);
    }

    public UnprocessableEntityException(String message) {
        super(message);
    }
}

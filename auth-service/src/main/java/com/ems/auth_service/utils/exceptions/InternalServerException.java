package com.ems.auth_service.utils.exceptions;

import org.springframework.security.core.AuthenticationException;

public class InternalServerException extends AuthenticationException {
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
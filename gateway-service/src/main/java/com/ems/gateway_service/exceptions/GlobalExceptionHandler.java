package com.ems.gateway_service.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<CustomErrorResponse> handleUnauthorized(UnauthorizedException e) {
        log.warn("Unauthorized: {}", e.getMessage());

        CustomErrorResponse error = new CustomErrorResponse(
                e.getMessage(),
                "Unauthorized",
                HttpStatus.UNAUTHORIZED.value());

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleResourceNotFound(NoResourceFoundException e) {
        log.warn("Resource not found: {}", e.getMessage());

        CustomErrorResponse error = new CustomErrorResponse(
                e.getMessage(),
                "Not Found",
                HttpStatus.NOT_FOUND.value());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleAccessDenied(NotFoundException e) {
        log.warn("Access denied: {}", e.getMessage());

        CustomErrorResponse error = new CustomErrorResponse(
                e.getMessage(),
                "Service Unavailable",
                HttpStatus.SERVICE_UNAVAILABLE.value());

        return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Illegal argument: {}", e.getMessage());

        CustomErrorResponse error = new CustomErrorResponse(
                e.getMessage(),
                "Bad Request",
                HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        String details = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation failed: {}", details);

        CustomErrorResponse error = new CustomErrorResponse(
                details,
                "Validation Failed",
                HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> handleGenericException(Exception e) {
        log.error("Unexpected error occurred", e);

        CustomErrorResponse error = new CustomErrorResponse(
                "An unexpected error occurred",
                "Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
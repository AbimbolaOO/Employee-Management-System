package com.ems.auth_service.utils.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleResourceNotFound(ResourceNotFoundException e) {
        log.warn("Resource not found: {}", e.getMessage());

        CustomErrorResponse error = new CustomErrorResponse(
                e.getMessage(),
                "Not Found",
                HttpStatus.NOT_FOUND.value());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleNoResourceFound(NoResourceFoundException e) {
        log.warn("Resource not found: {}", e.getMessage());

        CustomErrorResponse error = new CustomErrorResponse(
                e.getMessage(),
                "Not Found",
                HttpStatus.NOT_FOUND.value());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<CustomErrorResponse> handleUnauthorized(UnauthorizedException e) {
        log.warn("Unauthorized: {}", e.getMessage());

        CustomErrorResponse error = new CustomErrorResponse(
                e.getMessage(),
                "Unauthorized",
                HttpStatus.UNAUTHORIZED.value());

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomErrorResponse> handleAccessDenied(AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());

        CustomErrorResponse error = new CustomErrorResponse(
                e.getMessage(),
                "Forbidden",
                HttpStatus.FORBIDDEN.value());

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<CustomErrorResponse> handleUnprocessableEntityException(UnprocessableEntityException e) {
        log.warn("Unprocessable entity 422: {}", e.getMessage());

        CustomErrorResponse error = new CustomErrorResponse(
                e.getMessage(),
                "Forbidden",
                HttpStatus.UNPROCESSABLE_ENTITY.value());

        return new ResponseEntity<>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CustomErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        log.warn("Method not allowed: {}", e.getMessage());

        CustomErrorResponse error = new CustomErrorResponse(
                e.getMessage(),
                "Method Not Allowed",
                HttpStatus.METHOD_NOT_ALLOWED.value());

        return new ResponseEntity<>(error, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(UniqueConstraintViolationException.class)
    public ResponseEntity<CustomErrorResponse> handleUniqueConstraintViolation(UniqueConstraintViolationException e) {
        log.warn("Unique constraint violation: {}", e.getMessage());

        CustomErrorResponse error = new CustomErrorResponse(
                e.getMessage(),
                "Conflict",
                HttpStatus.CONFLICT.value());

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
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

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<CustomErrorResponse> handleJwtAuthenticationException(JwtAuthenticationException e) {
        log.warn("JWT authentication failed: {}", e.getMessage());

        CustomErrorResponse error = new CustomErrorResponse(
                e.getMessage(),
                "Unauthorized",
                HttpStatus.UNAUTHORIZED.value());

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
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

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<CustomErrorResponse> handleInternalServerExceptions(InternalServerException e) {
        log.warn("Internal server error: ", e);

        CustomErrorResponse error = new CustomErrorResponse(
                e.getMessage(),
                "Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
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
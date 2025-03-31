package com.ems.auth_service.utils.exceptions;

public record CustomErrorResponse(Object message, String error, int statusCode) {
}
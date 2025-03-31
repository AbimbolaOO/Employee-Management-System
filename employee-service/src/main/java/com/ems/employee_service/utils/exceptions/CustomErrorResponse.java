package com.ems.employee_service.utils.exceptions;

public record CustomErrorResponse(Object message, String error, int statusCode) {
}
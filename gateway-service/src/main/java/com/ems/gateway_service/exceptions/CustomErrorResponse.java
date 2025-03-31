package com.ems.gateway_service.exceptions;

public record CustomErrorResponse(Object message, String error, int statusCode) {
}
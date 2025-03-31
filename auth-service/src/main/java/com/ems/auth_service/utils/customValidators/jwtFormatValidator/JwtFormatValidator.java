package com.ems.auth_service.utils.customValidators.jwtFormatValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class JwtFormatValidator implements ConstraintValidator<ValidJwtFormat, String> {

    private static final String JWT_REGEX = "^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$";

    @Override
    public boolean isValid(String token, ConstraintValidatorContext context) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        return token.matches(JWT_REGEX);
    }
}

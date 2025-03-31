package com.ems.auth_service.utils.customValidators.jwtFormatValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = JwtFormatValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidJwtFormat {
    String message() default "Invalid JWT format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
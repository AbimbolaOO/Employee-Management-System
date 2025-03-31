package com.ems.auth_service.utils.customValidators.enumValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidatorImpl implements ConstraintValidator<EnumValidator, Enum<?>> {
    private Enum<?>[] enumValues;

    @Override
    public void initialize(EnumValidator constraintAnnotation) {
        enumValues = constraintAnnotation.enumClass().getEnumConstants();
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        for (Enum<?> enumValue : enumValues) {
            if (enumValue.name().equals(value.name())) {
                return true;
            }
        }
        return false;
    }
}
package com.ems.auth_service.utils.customValidators.strongPasswordValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {
    private int minLength;
    private int minUppercase;
    private int minLowercase;
    private int minNumbers;
    private int minSymbols;

    @Override
    public void initialize(StrongPassword constraintAnnotation) {
        this.minLength = constraintAnnotation.minLength();
        this.minUppercase = constraintAnnotation.minUppercase();
        this.minLowercase = constraintAnnotation.minLowercase();
        this.minNumbers = constraintAnnotation.minNumbers();
        this.minSymbols = constraintAnnotation.minSymbols();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.length() < minLength) return false;

        long uppercaseCount = password.chars().filter(Character::isUpperCase).count();
        long lowercaseCount = password.chars().filter(Character::isLowerCase).count();
        long digitCount = password.chars().filter(Character::isDigit).count();
        long symbolCount = password.chars().filter(ch -> "!@#$%^&*".indexOf(ch) >= 0).count();

        return uppercaseCount >= minUppercase &&
                lowercaseCount >= minLowercase &&
                digitCount >= minNumbers &&
                symbolCount >= minSymbols;
    }
}
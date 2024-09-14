package com.alandha.shopping_cart.configValidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NameValidator implements ConstraintValidator<NameValid, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null || value.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Your Full Name cannot be empty")
                    .addConstraintViolation();

            return false;
        }

        if(value.length() < 8 || value.length() > 28) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Your Full Name must be between 8 and 28 characters")
                    .addConstraintViolation();

            return false;
        }
        return true;
    }
}

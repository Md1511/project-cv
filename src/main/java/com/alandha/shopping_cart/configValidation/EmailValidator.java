package com.alandha.shopping_cart.configValidation;

import com.alandha.shopping_cart.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Email;
import jakarta.validation.executable.ExecutableValidator;
import jakarta.validation.metadata.BeanDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class EmailValidator implements ConstraintValidator<EmailValid, String> {

    @Autowired
    private UserService userService;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null || value.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Your email cannot be empty.")
                    .addConstraintViolation();
            return false;
        }
        if (!value.matches("^[^@\\s]+@[^@\\s]{3,}\\.[a-zA-Z]{2,}$")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Your email must contain '@', at least 3 characters after it, and a valid domain (e.g., .com).")
                    .addConstraintViolation();
            return false;
        }
        if(value.length() < 11 || value.length() > 55 ) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Your email must be between 11 and 55 characters.")
                    .addConstraintViolation();

            return false;
        }
        if(userService==null || userService.findUserByEmail(value) == null) {
            return true;
        }
        if(userService.findUserByEmail(value) != null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Your email is already in use.")
                    .addConstraintViolation();

            return false;
        }
        if(value.contains(" ")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Your email must not contain spaces.")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}

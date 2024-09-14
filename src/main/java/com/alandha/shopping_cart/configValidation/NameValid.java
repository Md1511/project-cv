package com.alandha.shopping_cart.configValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NameValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NameValid {

    String message() default "Your name should have between 8 and 28 characters";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}

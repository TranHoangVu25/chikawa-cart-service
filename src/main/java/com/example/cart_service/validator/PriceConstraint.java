package com.example.cart_service.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = {PriceValidator.class})
public @interface PriceConstraint {
    String message() default "Price must be higher than 0";

    double min();
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

package com.example.cart_service.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class PriceValidator implements ConstraintValidator<PriceConstraint, Double> {

    private double min;
    @Override
    public void initialize(PriceConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        min = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(Double price, ConstraintValidatorContext context) {
        if (Objects.isNull(price))
            return true;
         return price > min;
    }
}

package com.mazadak.auctions.validation.annotation;

import com.mazadak.auctions.validation.validator.ReservePriceValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReservePriceValidator.class)
@Documented
public @interface ValidReservePrice {
    String message() default "Reserve price cannot be less than starting price";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

package com.mazadak.auctions.validation.annotation;

import com.mazadak.auctions.validation.validator.ReservePriceValidator;
import com.mazadak.auctions.validation.validator.StartAndEndTimesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StartAndEndTimesValidator.class)
@Documented
public @interface ValidStartAndEndTimes {
    String message() default "Start time cannot be after end time";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

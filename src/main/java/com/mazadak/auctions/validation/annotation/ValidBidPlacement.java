package com.mazadak.auctions.validation.annotation;


import com.mazadak.auctions.validation.validator.ValidBidPlacementValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidBidPlacementValidator.class)
@Documented
public @interface ValidBidPlacement {
    String message() default "Invalid bid placement";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

package com.mazadak.auctions.validation.validator;

import com.mazadak.auctions.validation.annotation.ValidStartAndEndTimes;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class StartAndEndTimesValidator implements ConstraintValidator<ValidStartAndEndTimes, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            var startTime = (LocalDateTime) value.getClass().getMethod("startTime").invoke(value);
            var endTime = (LocalDateTime) value.getClass().getMethod("endTime").invoke(value);
            if (startTime == null || endTime == null) return false; // handled by @NotNull
            return startTime.isBefore(endTime);
        } catch (Exception e) {
            return true;
        }
    }
}

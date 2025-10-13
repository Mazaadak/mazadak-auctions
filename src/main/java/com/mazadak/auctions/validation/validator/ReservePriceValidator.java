package com.mazadak.auctions.validation.validator;

import com.mazadak.auctions.dto.request.UpdateAuctionRequest;
import com.mazadak.auctions.validation.annotation.ValidReservePrice;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class ReservePriceValidator implements ConstraintValidator<ValidReservePrice, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            var startingPrice = (BigDecimal) value.getClass().getMethod("startingPrice").invoke(value);
            var reservePrice = (BigDecimal) value.getClass().getMethod("reservePrice").invoke(value);

            if (reservePrice == null || startingPrice == null) {
                return true; // handled by @NotNull
            }

            return reservePrice.compareTo(startingPrice) >= 0;
        } catch (Exception e) {
            return true;
        }
    }
}

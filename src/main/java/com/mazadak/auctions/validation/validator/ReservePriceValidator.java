package com.mazadak.auctions.validation.validator;

import com.mazadak.auctions.dto.request.UpdateAuctionRequest;
import com.mazadak.auctions.validation.annotation.ValidReservePrice;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ReservePriceValidator implements ConstraintValidator<ValidReservePrice, UpdateAuctionRequest> {

    @Override
    public boolean isValid(UpdateAuctionRequest value, ConstraintValidatorContext context) {
        if (value.reservePrice() == null || value.startingPrice() == null) {
            return true; // handled by @NotNull
        }

        return value.reservePrice().compareTo(value.startingPrice()) >= 0;
    }
}

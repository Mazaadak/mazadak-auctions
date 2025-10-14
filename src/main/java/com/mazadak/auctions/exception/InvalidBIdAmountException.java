package com.mazadak.auctions.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidBIdAmountException extends RuntimeException {
    public InvalidBIdAmountException(BigDecimal bidAmount, BigDecimal minAllowedBid) {
        super(String.format("%s is insufficient bid amount, bid must be at least %s", bidAmount, minAllowedBid));
    }
}

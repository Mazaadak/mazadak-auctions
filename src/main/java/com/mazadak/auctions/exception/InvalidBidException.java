package com.mazadak.auctions.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidBidException extends RuntimeException {
    public InvalidBidException(String message) {
        super(message);
    }

    public InvalidBidException(BigDecimal bidAmount, BigDecimal minAllowedBid) {
        super(String.format("%s is insufficient bid amount, bid must be at least %s", bidAmount, minAllowedBid));
    }
}

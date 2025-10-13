package com.mazadak.auctions.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidAuctionOperationException extends RuntimeException {
    public InvalidAuctionOperationException(String message, Long id) {
        super(message + " (Auction id: " + id + ")");
    }
}

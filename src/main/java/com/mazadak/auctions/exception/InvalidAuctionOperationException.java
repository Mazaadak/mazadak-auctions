package com.mazadak.auctions.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidAuctionOperationException extends RuntimeException {
    public InvalidAuctionOperationException(String message, UUID id) {
        super(message + " (Auction id: " + id + ")");
    }
}

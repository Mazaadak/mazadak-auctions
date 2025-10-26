package com.mazadak.auctions.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductAlreadyHasAListedAuctionException extends RuntimeException {
    public ProductAlreadyHasAListedAuctionException(String message) {
        super(message);
    }
}

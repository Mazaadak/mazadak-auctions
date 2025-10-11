package com.mazadak.auctions.exception;

import com.mazadak.auctions.model.enumeration.AuctionStatus;

public class IllegalAuctionStatusTransitionException extends RuntimeException {
    private final AuctionStatus from, to;
    public IllegalAuctionStatusTransitionException(String message, AuctionStatus from, AuctionStatus to) {
        super(message);
        this.from = from;
        this.to = to;
    }
}

package com.mazadak.auctions.model.enumeration;

public enum AuctionStatus {
    SCHEDULED,
    STARTED,
    ACTIVE,
    ENDED,
    PAUSED,
    CANCELLED;

    public boolean canTransitionTo(AuctionStatus target) {
        return switch(this) {
            case SCHEDULED -> target == STARTED || target == CANCELLED;
            case STARTED -> target == ACTIVE || target == ENDED || target == PAUSED || target == CANCELLED;
            case ACTIVE -> target == ENDED;
            case ENDED, CANCELLED -> false;
            case PAUSED -> target == STARTED;
        };
    }
}

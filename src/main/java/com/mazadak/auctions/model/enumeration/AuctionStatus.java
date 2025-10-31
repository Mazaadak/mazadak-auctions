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
            case SCHEDULED -> target == STARTED || target == CANCELLED || target == SCHEDULED;
            case STARTED -> target == SCHEDULED || target == ACTIVE || target == ENDED || target == PAUSED || target == CANCELLED || target == STARTED;
            case ACTIVE -> target == ENDED || target == ACTIVE;
            case ENDED -> target == ENDED;
            case CANCELLED -> target == CANCELLED;
            case PAUSED -> target == STARTED || target == PAUSED || target == ENDED;
        };
    }
}

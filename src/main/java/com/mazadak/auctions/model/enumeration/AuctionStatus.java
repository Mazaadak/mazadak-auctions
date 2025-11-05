package com.mazadak.auctions.model.enumeration;

import java.util.EnumSet;

public enum AuctionStatus {
    SCHEDULED,
    STARTED,
    ACTIVE,
    ENDED,
    PAUSED,
    CANCELLED,
    INVALID, // no checkout
    COMPLETED; // checkout complete

    public boolean canTransitionTo(AuctionStatus target) {
        return switch(this) {
            case SCHEDULED -> EnumSet.of(STARTED, CANCELLED, SCHEDULED, COMPLETED, INVALID).contains(target);
            case STARTED -> EnumSet.of(SCHEDULED, ACTIVE, ENDED, PAUSED, CANCELLED, STARTED, COMPLETED, INVALID).contains(target);
            case ACTIVE -> EnumSet.of(ENDED, ACTIVE, COMPLETED, INVALID).contains(target);
            case ENDED -> EnumSet.of(ENDED, COMPLETED, INVALID).contains(target);
            case CANCELLED -> target == CANCELLED;
            case PAUSED -> EnumSet.of(STARTED, PAUSED, ENDED, COMPLETED, INVALID).contains(target);
            case COMPLETED -> target == COMPLETED;
            case INVALID -> target == INVALID;
        };
    }
}

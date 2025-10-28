package com.mazadak.auctions.dto.response;

import java.util.UUID;

public record AuctionWatchResponse(
        AuctionResponse auction,
        UUID userId,
        boolean notified) {
}

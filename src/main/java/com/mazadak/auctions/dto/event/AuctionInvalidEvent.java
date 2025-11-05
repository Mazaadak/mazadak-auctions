package com.mazadak.auctions.dto.event;

import java.util.UUID;

public record AuctionInvalidEvent(UUID auctionId) {
}

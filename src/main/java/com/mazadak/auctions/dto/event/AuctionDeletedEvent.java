package com.mazadak.auctions.dto.event;

import java.util.UUID;

public record AuctionDeletedEvent(UUID auctionId, UUID productId) {
}

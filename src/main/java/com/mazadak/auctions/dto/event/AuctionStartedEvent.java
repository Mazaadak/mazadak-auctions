package com.mazadak.auctions.dto.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuctionStartedEvent(UUID auctionId,
                                  String title,
                                  LocalDateTime startTime) {
}

package com.mazadak.auctions.dto.event;

import java.time.LocalDateTime;

public record AuctionStartedEvent(Long auctionId,
                                  String title,
                                  LocalDateTime startTime) {
}

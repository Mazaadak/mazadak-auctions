package com.mazadak.auctions.dto.event;

import com.mazadak.auctions.dto.response.AuctionWatchResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AuctionStartedEvent(UUID auctionId,
                                  UUID productId,
                                  String title,
                                  LocalDateTime startTime,
                                  List<AuctionWatchResponse> watchlist) {
}

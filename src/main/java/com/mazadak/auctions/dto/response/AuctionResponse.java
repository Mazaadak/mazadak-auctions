package com.mazadak.auctions.dto.response;

import com.mazadak.auctions.model.entity.Bid;
import com.mazadak.auctions.model.enumeration.AuctionStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link com.mazadak.auctions.model.entity.Auction}
 */
public record AuctionResponse(
        UUID id,
        UUID productId,
        UUID sellerId,
        String title,
        BigDecimal startingPrice,
        BigDecimal reservePrice,
        BidResponse highestBidPlaced,
        BigDecimal bidIncrement,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AuctionStatus status)
        implements Serializable {
  }
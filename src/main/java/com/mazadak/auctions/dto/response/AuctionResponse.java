package com.mazadak.auctions.dto.response;

import com.mazadak.auctions.model.entity.Bid;
import com.mazadak.auctions.model.enumeration.AuctionStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.mazadak.auctions.model.entity.Auction}
 */
public record AuctionResponse(
        Long id,
        Long productId,
        Long sellerId,
        String title,
        BigDecimal startingPrice,
        BigDecimal reservePrice,
        Bid highestBidPlaced,
        BigDecimal bidIncrement,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AuctionStatus status)
        implements Serializable {
  }
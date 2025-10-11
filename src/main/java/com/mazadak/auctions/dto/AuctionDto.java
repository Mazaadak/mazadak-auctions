package com.mazadak.auctions.dto;

import com.mazadak.auctions.model.enumeration.AuctionStatus;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.mazadak.auctions.model.entity.Auction}
 */
@Value
public class AuctionDto implements Serializable {
    Long id;
    Instant createdAt;
    Long productId;
    Long sellerId;
    String title;
    BigDecimal highestBidPlaced;
    LocalDateTime startTime;
    LocalDateTime endTime;
    AuctionStatus status;
}
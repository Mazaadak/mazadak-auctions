package com.mazadak.auctions.dto.request;

import com.mazadak.auctions.model.enumeration.AuctionStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.mazadak.auctions.model.entity.Auction}
 */
@Value
public class CreateAuctionRequest implements Serializable {
    Long productId;
    Long sellerId;
    String title;
    BigDecimal startingPrice;
    BigDecimal reservePrice;
    BigDecimal highestBidPlaced;
    BigDecimal bidIncrement;
    LocalDateTime startTime;
    LocalDateTime endTime;
    AuctionStatus status;
}
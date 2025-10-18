package com.mazadak.auctions.dto.request;

import com.mazadak.auctions.validation.annotation.ValidReservePrice;
import com.mazadak.auctions.validation.annotation.ValidStartAndEndTimes;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link com.mazadak.auctions.model.entity.Auction}
 */
@ValidReservePrice
@ValidStartAndEndTimes
public record CreateAuctionRequest(
        @NotNull(message = "An auction must be associated with a product") UUID productId,
        @NotNull(message = "An auction must be associated with a seller") UUID sellerId,
        @NotBlank(message = "Auction title cannot be blank") String title,
        @NotNull(message = "An auction must have a starting price") @Positive(message = "Auction starting price must be positive") BigDecimal startingPrice,
        @Positive(message = "Reserve price must be positive") BigDecimal reservePrice,
        @NotNull(message = "Bid increment cannot be null") @Min(message = "Bid increment must at least be $1", value = 1) BigDecimal bidIncrement,
        @NotNull(message = "An auction must have a start time") @Future(message = "An auction's start time cannot be in the past") LocalDateTime startTime,
        @Future(message = "An auction's end time cannot be in the past") LocalDateTime endTime)
        implements Serializable {
  }
package com.mazadak.auctions.dto.request;

import com.mazadak.auctions.validation.annotation.ValidReservePrice;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.mazadak.auctions.model.entity.Auction}
 */
@ValidReservePrice
public record UpdateAuctionRequest(@NotNull(message = "An auction must be associated with a product") Long productId,
                                   @NotBlank(message = "Auction title cannot be blank") String title,
                                   @NotNull(message = "Starting price cannot be empty") @Min(message = "Starting price cannot be less than $1", value = 1) BigDecimal startingPrice,
                                   BigDecimal reservePrice,
                                   @NotNull(message = "You must specify a bid increment") @Min(message = "Bid increment cannot be less than $1", value = 1) BigDecimal bidIncrement,
                                   @NotNull(message = "You must specify a start time") LocalDateTime startTime,
                                   @NotNull(message = "You must specify an end time") @Future(message = "The end time must be in the future") LocalDateTime endTime) implements Serializable {
}
package com.mazadak.auctions.dto.request;

import com.mazadak.auctions.validation.annotation.ValidBidPlacement;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for {@link com.mazadak.auctions.model.entity.Bid}
 */
@Value
@ValidBidPlacement
public class PlaceBidRequest implements Serializable {
    @NotNull(message = "A bid must be associated with an auction") Long auctionId;
    @NotNull(message = "A bid must be associated with a bidder") Long bidderId;

    @NotNull(message = "A bid must have an amount")
    @Positive(message = "Bid amount must be positive")
    BigDecimal amount;

    String idempotencyKey;
}


// @NotNull(message = "An auction must be associated with a product")
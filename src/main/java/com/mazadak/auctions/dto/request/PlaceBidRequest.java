package com.mazadak.auctions.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 * DTO for {@link com.mazadak.auctions.model.entity.Bid}
 */
@Schema(
        name = "Place Bid Request",
        description = "Schema to Hold Place Bid Request Information"
)
@Value
public class PlaceBidRequest implements Serializable {
    @Schema(description = "Identifier of the bidder placing the bid", example = "456", required = true)
    @NotNull(message = "A bid must be associated with a bidder")
    Long bidderId;

    @Schema(description = "Amount of the bid in the auction currency", example = "100.00", required = true, type = "number", format = "decimal")
    @NotNull(message = "A bid must have an amount")
    @Positive(message = "Bid amount must be positive")
    BigDecimal amount;
}
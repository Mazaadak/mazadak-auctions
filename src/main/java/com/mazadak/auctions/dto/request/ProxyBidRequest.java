package com.mazadak.auctions.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link com.mazadak.auctions.model.entity.ProxyBid}
 */
@Schema(
        name = "Create or Update Proxy Bid Request",
        description = "Schema to Hold Place Proxy Bid Request Information For Creation or Deletion"
)
@Value
// TODO: Change example when changing Id to UUID
public class ProxyBidRequest implements Serializable {
    @Schema(description = "Identifier of the auction the bid is placed on", example = "123", required = true)
    @NotNull(message = "A proxy bid must be associated with an auction")
    Long auctionId;

    @Schema(description = "Identifier of the bidder placing the bid", example = "456", required = true)
    @NotNull(message = "A proxy bid must be associated with a bidder")
    Long bidderId;

    @Schema(description = "Max bid amount that the bidder is willing to bid in the auction currency", example = "500.00", required = true, type = "number", format = "decimal")
    @NotNull(message = "A proxy bid must have a max amount")
    @Positive(message = "A proxy bid must be positive")
    BigDecimal maxAmount;

    @Schema(description = "Idempotency key to prevent duplicate bids. Optional.", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")

    String idempotencyKey;
}
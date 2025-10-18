package com.mazadak.auctions.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;


/**
 * DTO for {@link com.mazadak.auctions.model.entity.Bid}
 */
@Schema(
        name = "BidResponse",
        description = "Bid details returned by the API"
)
@Value
public class BidResponse implements Serializable {

    @Schema(description = "Unique identifier of the bid", example = "123")
    Long id;

    @Schema(description = "Identifier of the auction this bid belongs to", example = "45")
    Long auctionId;

    @Schema(description = "Identifier of the user who placed the bid", example = "789")
    Long bidderId;

    @Schema(description = "Bid amount", example = "100.00")
    BigDecimal amount;

    // TODO: remove
    @Schema(description = "Idempotency key used when placing the bid", example = "a1b2c3d4-e5f6-7g8h-9i0j")
    String idempotencyKey;
}
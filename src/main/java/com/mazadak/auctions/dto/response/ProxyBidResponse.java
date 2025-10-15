package com.mazadak.auctions.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link com.mazadak.auctions.model.entity.ProxyBid}
 */
@Schema(
        name = "Proxy Bid Response",
        description = "Proxy Bid details returned by the API"
)
@Value
public class ProxyBidResponse implements Serializable {

    @Schema(description = "Unique identifier of the proxy bid", example = "123")
    Long id;

    @Schema(description = "Identifier of the auction this proxy bid belongs to", example = "45")
    Long auctionId;

    @Schema(description = "Identifier of the user who placed the proxy bid", example = "789")
    Long bidderId;

    @Schema(description = "Bid amount", example = "500.00")
    BigDecimal maxAmount;

    @Schema(description = "Idempotency key used when placing the bid", example = "a1b2c3d4-e5f6-7g8h-9i0j")
    String idempotencyKey;
}
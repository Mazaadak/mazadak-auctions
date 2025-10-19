package com.mazadak.auctions.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;


/**
 * DTO for {@link com.mazadak.auctions.model.entity.Bid}
 */
@Schema(
        name = "BidResponse",
        description = "Bid details returned by the API"
)
public record BidResponse(
        UUID id,
        UUID auctionId,
        UUID bidderId,
        BigDecimal amount,
        String idempotencyKey
) implements Serializable { }
package com.mazadak.auctions.dto.response;

import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;


/**
 * DTO for {@link com.mazadak.auctions.model.entity.Bid}
 */
@Value
public class BidResponse implements Serializable {
    Long id;
    Long auctionId;
    Long bidderId;
    BigDecimal amount;
    String idempotencyKey;
}
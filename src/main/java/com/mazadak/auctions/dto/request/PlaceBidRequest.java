package com.mazadak.auctions.dto.request;

import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for {@link com.mazadak.auctions.model.entity.Bid}
 */
@Value

public class PlaceBidRequest implements Serializable {
    Long auctionId;
    Long bidderId;
    BigDecimal amount;
    String idempotencyKey;
}

/* TODO: Validation
    auction.status == ACTIVE (if not make it active + check status transition rules in auctions)
    amount >= current_highest_bid + increment
    auction.start_time <= now < auction.end_time
    bidder != seller
* */
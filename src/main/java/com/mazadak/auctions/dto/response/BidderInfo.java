package com.mazadak.auctions.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record BidderInfo(
        UUID id,
        BigDecimal amount,
        String email
) {
}

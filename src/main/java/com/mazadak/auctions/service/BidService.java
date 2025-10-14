package com.mazadak.auctions.service;

import com.mazadak.auctions.dto.request.PlaceBidRequest;
import com.mazadak.auctions.dto.response.BidResponse;

import java.math.BigDecimal;

public interface BidService {
    BidResponse placeBid(PlaceBidRequest request);
    BigDecimal getHighestBid(Long auctionId);
}

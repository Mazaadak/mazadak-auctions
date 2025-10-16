package com.mazadak.auctions.service;

import com.mazadak.auctions.dto.request.PlaceBidRequest;
import com.mazadak.auctions.dto.response.BidResponse;
import com.mazadak.auctions.model.entity.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface BidService {
    BidResponse placeBid(PlaceBidRequest request, Long auctionId, String idempotencyKey);
    BigDecimal getHighestBid(Long auctionId);
    Page<BidResponse> getBids(Long id, Long bidderId, Pageable pageable);
    Page<BidResponse> getBidsByBidder(Long bidderId, Pageable pageable);
}

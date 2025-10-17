package com.mazadak.auctions.service;

import com.mazadak.auctions.dto.request.PlaceBidRequest;
import com.mazadak.auctions.dto.response.BidResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface BidService {
    BidResponse placeBid(PlaceBidRequest request, UUID auctionId, String idempotencyKey);
    BidResponse getHighestBid(UUID auctionId);
    Page<BidResponse> getBids(UUID id, UUID bidderId, Pageable pageable);
    Page<BidResponse> getBidsByBidder(UUID bidderId, Pageable pageable);
}

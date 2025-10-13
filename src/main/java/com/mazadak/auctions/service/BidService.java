package com.mazadak.auctions.service;

import com.mazadak.auctions.dto.request.PlaceBidRequest;
import com.mazadak.auctions.dto.response.BidResponse;

public interface BidService {
    BidResponse placeBid(PlaceBidRequest request);
}

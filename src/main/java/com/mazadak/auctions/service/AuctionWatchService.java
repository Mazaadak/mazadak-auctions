package com.mazadak.auctions.service;

import com.mazadak.auctions.dto.response.AuctionWatchResponse;

import java.util.List;
import java.util.UUID;

public interface AuctionWatchService {
    List<AuctionWatchResponse> getById(UUID auctionId);
}

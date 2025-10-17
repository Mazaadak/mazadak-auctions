package com.mazadak.auctions.service;

import com.mazadak.auctions.dto.request.ProxyBidRequest;
import com.mazadak.auctions.dto.response.ProxyBidResponse;
import com.mazadak.auctions.util.UpsertResult;

import java.util.UUID;

public interface ProxyBidService {
    UpsertResult upsertProxyBid(ProxyBidRequest request, UUID auctionId, UUID bidderId);

    ProxyBidResponse getProxyBid(UUID auctionId, UUID bidderId);

    void deleteProxyBid(UUID auctionId, UUID bidderId);
}

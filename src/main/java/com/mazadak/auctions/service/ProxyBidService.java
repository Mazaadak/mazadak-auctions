package com.mazadak.auctions.service;

import com.mazadak.auctions.dto.request.ProxyBidRequest;
import com.mazadak.auctions.dto.response.ProxyBidResponse;
import com.mazadak.auctions.model.entity.Auction;
import com.mazadak.auctions.util.ProxyBidUpsertResult;

public interface ProxyBidService {
    ProxyBidUpsertResult upsertProxyBid(ProxyBidRequest request, Long auctionId, Long bidderId);
    ProxyBidResponse getProxyBid(Long auctionId, Long bidderId);
    void deleteProxyBid(Long auctionId, Long bidderId);
    void triggerProxyBidding(Auction auction);
}

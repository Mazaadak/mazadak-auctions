package com.mazadak.auctions.service;

import com.mazadak.auctions.dto.request.ProxyBidRequest;
import com.mazadak.auctions.service.support.UpsertResult;

public interface ProxyBidService {
    UpsertResult upsertProxyBid(ProxyBidRequest request, Long auctionId, Long bidderId);
}

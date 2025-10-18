package com.mazadak.auctions.service;

import com.mazadak.auctions.dto.request.ProxyBidRequest;
import com.mazadak.auctions.dto.response.ProxyBidResponse;
import com.mazadak.auctions.model.entity.Auction;
import com.mazadak.auctions.util.ProxyBidUpsertResult;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface ProxyBidService {
    ProxyBidUpsertResult upsertProxyBid(ProxyBidRequest request, UUID auctionId, UUID bidderId);

    ProxyBidResponse getProxyBid(UUID auctionId, UUID bidderId);

    void deleteProxyBid(UUID auctionId, UUID bidderId);

    @Transactional(propagation = Propagation.MANDATORY)
    void triggerProxyBidding(Auction auction);
}

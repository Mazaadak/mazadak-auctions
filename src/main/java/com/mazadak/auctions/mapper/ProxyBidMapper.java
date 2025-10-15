package com.mazadak.auctions.mapper;

import com.mazadak.auctions.dto.request.ProxyBidRequest;
import com.mazadak.auctions.dto.response.ProxyBidResponse;
import com.mazadak.auctions.model.entity.ProxyBid;

public class ProxyBidMapper {
    public static ProxyBid toEntity(ProxyBidRequest dto) {
        return new ProxyBid(
                dto.getAuctionId(),
                dto.getBidderId(),
                dto.getMaxAmount(),
                dto.getIdempotencyKey()
        );
    }

    public static ProxyBidResponse toBidResponse(ProxyBid proxyBid) {
        return new ProxyBidResponse(
                proxyBid.getId(),
                proxyBid.getAuctionId(),
                proxyBid.getBidderId(),
                proxyBid.getMaxAmount(),
                proxyBid.getIdempotencyKey()
        );
    }

}

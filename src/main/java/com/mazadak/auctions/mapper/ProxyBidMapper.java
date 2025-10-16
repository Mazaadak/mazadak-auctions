package com.mazadak.auctions.mapper;

import com.mazadak.auctions.dto.request.ProxyBidRequest;
import com.mazadak.auctions.dto.response.ProxyBidResponse;
import com.mazadak.auctions.model.entity.ProxyBid;

public class ProxyBidMapper {
    public static ProxyBid toEntity(ProxyBidRequest dto, Long auctionId, Long bidderId) {
        return new ProxyBid(
                auctionId,
                bidderId,
                dto.getMaxAmount()
        );
    }

    public static ProxyBidResponse toResponseDto(ProxyBid proxyBid) {
        return new ProxyBidResponse(
                proxyBid.getId(),
                proxyBid.getAuctionId(),
                proxyBid.getBidderId(),
                proxyBid.getMaxAmount(),
                proxyBid.getCreatedAt(),
                proxyBid.getUpdatedAt()
        );
    }

}

package com.mazadak.auctions.mapper;

import com.mazadak.auctions.dto.response.AuctionWatchResponse;
import com.mazadak.auctions.model.entity.AuctionWatch;

public class AuctionWatchMapper {
    public static AuctionWatchResponse toResponse(AuctionWatch auctionWatch) {
        return new AuctionWatchResponse(
                AuctionMapper.toResponseDto(auctionWatch.getAuction()),
                auctionWatch.getUserId(),
                auctionWatch.isNotified()
        );
    }
}

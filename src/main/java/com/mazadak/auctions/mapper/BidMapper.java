package com.mazadak.auctions.mapper;

import com.mazadak.auctions.dto.request.PlaceBidRequest;
import com.mazadak.auctions.dto.response.BidResponse;
import com.mazadak.auctions.model.entity.Bid;

public class BidMapper {
    public static Bid toEntity(PlaceBidRequest dto, Long auctionId, String idempotencyKey) {
        return new Bid(
                auctionId,
                dto.getBidderId(),
                dto.getAmount(),
                idempotencyKey
        );
    }

    public static BidResponse toResponseDto(Bid bid) {
        return new BidResponse(
                bid.getId(),
                bid.getAuctionId(),
                bid.getBidderId(),
                bid.getAmount(),
                bid.getIdempotencyKey()
        );
    }
}

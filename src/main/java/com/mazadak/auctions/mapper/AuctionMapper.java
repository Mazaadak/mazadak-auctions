package com.mazadak.auctions.mapper;

import com.mazadak.auctions.dto.AuctionDto;
import com.mazadak.auctions.dto.request.CreateAuctionRequest;
import com.mazadak.auctions.model.entity.Auction;

public class AuctionMapper {
    public static AuctionDto toDto(Auction auction) {
        return new AuctionDto(
                auction.getId(),
                auction.getCreatedAt(),
                auction.getProductId(),
                auction.getSellerId(),
                auction.getTitle(),
                auction.getHighestBidPlaced(),
                auction.getStartTime(),
                auction.getEndTime(),
                auction.getStatus()
        );
    }

    public static Auction toEntity(CreateAuctionRequest dto) {
        return new Auction(
                dto.getProductId(),
                dto.getSellerId(),
                dto.getTitle(),
                dto.getStartingPrice(),
                dto.getReservePrice(),
                dto.getHighestBidPlaced(),
                dto.getBidIncrement(),
                dto.getStartTime(),
                dto.getEndTime(),
                dto.getStatus(),
                1L
        );
    }
}

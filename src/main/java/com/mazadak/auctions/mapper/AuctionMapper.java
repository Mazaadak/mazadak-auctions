package com.mazadak.auctions.mapper;

import com.mazadak.auctions.dto.request.CreateAuctionRequest;
import com.mazadak.auctions.dto.response.AuctionResponse;
import com.mazadak.auctions.model.entity.Auction;
import com.mazadak.auctions.model.enumeration.AuctionStatus;

import java.time.LocalDateTime;

public class AuctionMapper {
//    public static AuctionDto toDto(Auction auction) {
//        return new AuctionDto(
//                auction.getId(),
//                auction.getCreatedAt(),
//                auction.getProductId(),
//                auction.getSellerId(),
//                auction.getTitle(),
//                auction.getHighestBidPlaced(),
//                auction.getStartTime(),
//                auction.getEndTime(),
//                auction.getStatus()
//        );
//    }

    public static Auction toEntity(CreateAuctionRequest dto) {
        return new Auction(
                dto.productId(),
                dto.sellerId(),
                dto.title(),
                dto.startingPrice(),
                dto.reservePrice(),
                null,
                dto.bidIncrement(),
                dto.startTime(),
                dto.endTime(),
                AuctionStatus.SCHEDULED
        );
    }

    public static AuctionResponse toResponseDto(Auction auction) {
        return new AuctionResponse(
                auction.getId(),
                auction.getProductId(),
                auction.getSellerId(),
                auction.getTitle(),
                auction.getStartingPrice(),
                auction.getReservePrice(),
                auction.getHighestBidPlaced() == null ? null : BidMapper.toResponseDto(auction.getHighestBidPlaced()),
                auction.getBidIncrement(),
                auction.getStartTime(),
                auction.getEndTime(),
                auction.getStatus()
        );
    }
}

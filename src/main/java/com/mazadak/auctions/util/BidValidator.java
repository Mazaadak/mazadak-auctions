package com.mazadak.auctions.util;

import com.mazadak.auctions.model.entity.Auction;
import com.mazadak.auctions.model.entity.Bid;
import com.mazadak.auctions.model.enumeration.AuctionStatus;
import com.mazadak.common.exception.domain.auction.InvalidBidException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public class BidValidator {

    public void validateBid(Auction auction, BigDecimal amount, UUID bidderId) {
        validateAuctionStatus(auction);
        validateSellerIsNotBidder(auction, bidderId);
        validateAuctionTimeWindow(auction);
        validateMinimumBid(auction, amount);
    }

    public void validateAuctionStatus(Auction auction) {
        AuctionStatus status = auction.getStatus();
        if (!(status.equals(AuctionStatus.STARTED) || status.equals(AuctionStatus.ACTIVE))) {
            throw new InvalidBidException("Cannot place bid: auction has not started. Auction Id: " + auction.getId());
        }
    }

    public void validateSellerIsNotBidder(Auction auction, UUID bidderId) {
        if (Objects.equals(bidderId, auction.getSellerId())) {
            throw new InvalidBidException("Seller cannot bid on their own auction");
        }
    }

    public void validateAuctionTimeWindow(Auction auction) {
        Instant now = Instant.now();
        Instant startTime = auction.getStartTime().toInstant(ZoneOffset.UTC);
        Instant endTime = auction.getEndTime().toInstant(ZoneOffset.UTC);
        if (now.isBefore(startTime) || now.isAfter(endTime)) {
            throw new InvalidBidException(String.format("Auction is not accepting bids at this time. Valid window: %s - %s (UTC)",
                    startTime, endTime));
        }
    }

    // TODO: Handle problem like someone adding 0.0000000000001 on the minAllowedBid
    public void validateMinimumBid(Auction auction, BigDecimal amount) {
        BigDecimal currentHighestBid = Optional.ofNullable(auction.getHighestBidPlaced())
                .map(Bid::getAmount)
                .orElse(auction.getStartingPrice());
        BigDecimal minAllowedBid = currentHighestBid.add(auction.getBidIncrement());
        if (amount.compareTo(minAllowedBid) < 0) {
            throw new InvalidBidException(amount, minAllowedBid);
        }
    }
}

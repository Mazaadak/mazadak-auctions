package com.mazadak.auctions.service.impl;

import com.mazadak.auctions.dto.request.PlaceBidRequest;
import com.mazadak.auctions.dto.response.BidResponse;
import com.mazadak.auctions.exception.InvalidBidException;
import com.mazadak.auctions.exception.ResourceNotFoundException;
import com.mazadak.auctions.mapper.BidMapper;
import com.mazadak.auctions.model.entity.Auction;
import com.mazadak.auctions.model.entity.Bid;
import com.mazadak.auctions.model.enumeration.AuctionStatus;
import com.mazadak.auctions.repository.AuctionRepository;
import com.mazadak.auctions.repository.BidRepository;
import com.mazadak.auctions.service.BidService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BidServiceImpl implements BidService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;

    @Transactional
    @Override
    public BidResponse placeBid(PlaceBidRequest request, Long auctionId) {
        String idempotencyKey = request.getIdempotencyKey();

        if (idempotencyKey != null) {
            Optional<Bid> existingBid = bidRepository.findByIdempotencyKey(idempotencyKey);
            if (existingBid.isPresent()) {
                return BidMapper.toBidResponse(existingBid.get());
            }
        }

        // This locks the auction record, other threads are allowed to read only.
        Auction auction = auctionRepository.findByIdForUpdate(request.getAuctionId()).orElseThrow(
                () -> new ResourceNotFoundException("Auction", "Id", request.getAuctionId().toString())
        );

        validateAuctionStatus(auction);
        validateSellerIsNotBidder(auction, request.getBidderId());
        validateAuctionTimeWindow(auction);
        validateMinimumBid(auction, request.getAmount());

        if (auction.getStatus().equals(AuctionStatus.STARTED) && bidRepository.countByAuctionId(auction.getId()) == 0L) {
            auction.setStatus(AuctionStatus.ACTIVE);
        }

        Bid newBid = new Bid(
                auction.getId(),
                request.getBidderId(),
                request.getAmount(),
                request.getIdempotencyKey()
        );

        bidRepository.save(newBid);
        auction.setHighestBidPlaced(newBid.getAmount());
        auctionRepository.save(auction);

        return BidMapper.toBidResponse(newBid);
    }

    private void validateAuctionStatus(Auction auction) {
        AuctionStatus status = auction.getStatus();
        if (!(status.equals(AuctionStatus.STARTED) || status.equals(AuctionStatus.ACTIVE))) {
            throw new InvalidBidException("Cannot place bid: auction has not started. Auction Id: " + auction.getId());
        }
    }

    private void validateSellerIsNotBidder(Auction auction, Long bidderId) {
        if (Objects.equals(bidderId, auction.getSellerId())) {
            throw new InvalidBidException("Seller cannot bid on their own auction");
        }
    }

    private void validateAuctionTimeWindow(Auction auction) {
        Instant now = Instant.now();
        Instant startTime = auction.getStartTime().toInstant(ZoneOffset.UTC);
        Instant endTime = auction.getEndTime().toInstant(ZoneOffset.UTC);
        if (now.isBefore(startTime) || now.isAfter(endTime)) {
            throw new InvalidBidException(String.format("Auction is not accepting bids at this time. Valid window: %s - %s (UTC)",
                    startTime, endTime));
        }
    }

    private void validateMinimumBid(Auction auction, BigDecimal amount) {
        BigDecimal currentHighestBid = Optional.ofNullable(auction.getHighestBidPlaced()).orElse(auction.getStartingPrice());
        BigDecimal minAllowedBid = currentHighestBid.add(auction.getBidIncrement());
        if (amount.compareTo(minAllowedBid) < 0) {
            throw new InvalidBidException(amount, minAllowedBid);
        }
    }

    @Override
    public BigDecimal getHighestBid(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(
                () -> new ResourceNotFoundException("Auction", "auctionId", auctionId.toString())
        );

        return auction.getHighestBidPlaced();
    }

    @Override
    public Page<BidResponse> getBids(Long id, Long bidderId, Pageable pageable) {
        Auction auction = auctionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Auction", "auctionId", id.toString())
        );

        Page<Bid> bids = (bidderId != null
                ? bidRepository.findByAuctionIdAndBidderId(id, bidderId, pageable)
                : bidRepository.findByAuctionId(id, pageable));

        return bids.map(BidMapper::toBidResponse);
    }

    @Override
    public Page<BidResponse> getBidsByBidder(Long bidderId, Pageable pageable) {
        Page<Bid> bids = bidRepository.findByBidderId(bidderId, pageable);
        return bids.map(BidMapper::toBidResponse);
    }

}

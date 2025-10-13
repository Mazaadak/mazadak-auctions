package com.mazadak.auctions.service.impl;

import com.mazadak.auctions.dto.request.PlaceBidRequest;
import com.mazadak.auctions.dto.response.BidResponse;
import com.mazadak.auctions.exception.ResourceNotFoundException;
import com.mazadak.auctions.mapper.BidMapper;
import com.mazadak.auctions.model.entity.Auction;
import com.mazadak.auctions.model.entity.Bid;
import com.mazadak.auctions.model.enumeration.AuctionStatus;
import com.mazadak.auctions.repository.AuctionRepository;
import com.mazadak.auctions.repository.BidRepository;
import com.mazadak.auctions.service.BidService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BidServiceImpl implements BidService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;

    @Transactional
    @Override
    public BidResponse placeBid(PlaceBidRequest request) {
        String idemKey = request.getIdempotencyKey();

        if (idemKey != null) {
            Optional<Bid> existingBid = bidRepository.findByIdempotencyKey(idemKey);
            if (existingBid.isPresent()) {
                return BidMapper.toBidResponse(existingBid.get());
            }
        }

        Auction auction = auctionRepository.findByIdForUpdate(request.getAuctionId()).orElseThrow(
                () -> new ResourceNotFoundException("Auction", "Id", request.getAuctionId().toString())
        );

        // TODO: Check If you can do the validation using declarative annotations instead programmatic validation
        BigDecimal currentHighestBidPlaced = (auction.getHighestBidPlaced() != null
                ? auction.getHighestBidPlaced() : auction.getStartingPrice());
        BigDecimal minAllowedBid = currentHighestBidPlaced.add(auction.getBidIncrement());

        if (request.getAmount().compareTo(minAllowedBid) < 0) {
            throw new IllegalArgumentException("Bid must be >= " + minAllowedBid);
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
}

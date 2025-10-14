package com.mazadak.auctions.service.impl;

import com.mazadak.auctions.dto.request.PlaceBidRequest;
import com.mazadak.auctions.dto.response.BidResponse;
import com.mazadak.auctions.exception.InvalidBIdAmountException;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class BidServiceImpl implements BidService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;

    /**
     * Places a bid for the given auction.
     *
     * <p>Behavior:
     * <ol>
     *   <li>If an idempotency key is provided and a bid with that key already exists, returns the existing bid.</li>
     *   <li>Loads the auction with a lock (via {@code findByIdForUpdate}) and throws {@link ResourceNotFoundException}
     *       if the auction does not exist.</li>
     *   <li>If the auction is in {@link AuctionStatus#STARTED} and this is the first bid, transitions the auction to
     *       {@link AuctionStatus#ACTIVE}.</li>
     *   <li>Validates that the requested bid amount is at least the current highest bid (or starting price) plus the
     *       auction's bid increment; throws {@link IllegalArgumentException} if the amount is too low.</li>
     *   <li>Persists the new {@link Bid} and updates the auction's highest bid.</li>
     * </ol>
     *
     * @param request the {@link PlaceBidRequest} containing auctionId, bidderId, amount and optional idempotencyKey
     * @return a {@link BidResponse} representing the created or existing bid
     * @throws ResourceNotFoundException if the auction with the given id cannot be found
     * @throws IllegalArgumentException if the bid amount is below the minimum allowed
     */
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

        if (auction.getStatus().equals(AuctionStatus.STARTED) && bidRepository.countByAuctionId(auction.getId()) == 0L) {
            auction.setStatus(AuctionStatus.ACTIVE);
        }

        BigDecimal currentHighestBid = Optional.ofNullable(auction.getHighestBidPlaced()).orElse(auction.getStartingPrice());
        BigDecimal minAllowedBid = currentHighestBid.add(auction.getBidIncrement());
        if (request.getAmount().compareTo(minAllowedBid) < 0) {
            throw new InvalidBIdAmountException(request.getAmount(), minAllowedBid);
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

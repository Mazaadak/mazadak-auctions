package com.mazadak.auctions.service.impl;

import com.mazadak.auctions.dto.request.PlaceBidRequest;
import com.mazadak.auctions.dto.response.BidResponse;
import com.mazadak.auctions.dto.response.BidderInfo;
import com.mazadak.common.exception.shared.ResourceNotFoundException;
import com.mazadak.auctions.mapper.BidMapper;
import com.mazadak.auctions.model.entity.Auction;
import com.mazadak.auctions.model.entity.Bid;
import com.mazadak.auctions.model.enumeration.AuctionStatus;
import com.mazadak.auctions.repository.AuctionRepository;
import com.mazadak.auctions.repository.BidRepository;
import com.mazadak.auctions.service.BidService;
import com.mazadak.auctions.service.ProxyBidService;
import com.mazadak.auctions.util.BidValidator;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BidServiceImpl implements BidService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final BidValidator bidValidator;
    private final ProxyBidService proxyBidService;

    @Transactional
    @Override
    public BidResponse placeBid(PlaceBidRequest request, UUID auctionId, String idempotencyKey) {
        Optional<Bid> existingBid = bidRepository.findByIdempotencyKey(idempotencyKey);
        if (existingBid.isPresent()) {
            return BidMapper.toResponseDto(existingBid.get());
        }

        Auction auction = lockAndValidateAuction(request, auctionId);
        activateAuctionIfFirstBid(auction);
        Bid newBid = createAndSaveBid(request, auctionId, idempotencyKey, auction);
        proxyBidService.triggerProxyBidding(auction);
        return BidMapper.toResponseDto(newBid);
    }

    private Auction lockAndValidateAuction(PlaceBidRequest request, UUID auctionId) {
        Auction auction = auctionRepository.findByIdForUpdate(auctionId).orElseThrow(
                () -> new ResourceNotFoundException("Auction", "Id", auctionId.toString())
        );

        bidValidator.validateBid(auction, request.getAmount(), request.getBidderId());
        return auction;
    }

    private void activateAuctionIfFirstBid(Auction auction) {
        if (auction.getStatus().equals(AuctionStatus.STARTED) && bidRepository.countByAuctionId(auction.getId()) == 0L) {
            auction.setStatus(AuctionStatus.ACTIVE);
        }
    }

    private Bid createAndSaveBid(PlaceBidRequest request, UUID auctionId, String idempotencyKey, Auction auction) {
        Bid newBid = BidMapper.toEntity(request, auctionId, idempotencyKey);
        bidRepository.save(newBid);
        auction.setHighestBidPlaced(newBid);
        auctionRepository.save(auction);
        return newBid;
    }

    @Override
    public BidResponse getHighestBid(UUID auctionId) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(
                () -> new ResourceNotFoundException("Auction", "auctionId", auctionId.toString())
        );

        return BidMapper.toResponseDto(auction.getHighestBidPlaced());
    }

    @Override
    public Page<BidResponse> getBids(UUID id, UUID bidderId, Pageable pageable) {
        Auction auction = auctionRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Auction", "auctionId", id.toString())
        );

        Page<Bid> bids = (bidderId != null
                ? bidRepository.findByAuctionIdAndBidderId(id, bidderId, pageable)
                : bidRepository.findByAuctionId(id, pageable));

        return bids.map(BidMapper::toResponseDto);
    }

    @Override
    public Page<BidResponse> getBidsByBidder(UUID bidderId, Pageable pageable) {
        Page<Bid> bids = bidRepository.findByBidderId(bidderId, pageable);
        return bids.map(BidMapper::toResponseDto);
    }

    @Override
    public List<BidderInfo> getHighestBidForEachBidderAboveReservePrice(UUID auctionId) {
        return bidRepository.findHighestBidsPerBidderByAuctionIdAboveReservePrice(auctionId).stream()
                .map(BidMapper::toBidderInfo)
                .toList();
    }

}

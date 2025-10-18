package com.mazadak.auctions.service.impl;


import com.mazadak.auctions.dto.request.ProxyBidRequest;
import com.mazadak.auctions.dto.response.ProxyBidResponse;
import com.mazadak.auctions.exception.ResourceNotFoundException;
import com.mazadak.auctions.mapper.ProxyBidMapper;
import com.mazadak.auctions.model.entity.Auction;
import com.mazadak.auctions.model.entity.Bid;
import com.mazadak.auctions.model.entity.ProxyBid;
import com.mazadak.auctions.repository.AuctionRepository;
import com.mazadak.auctions.repository.BidRepository;
import com.mazadak.auctions.repository.ProxyBidRepository;
import com.mazadak.auctions.service.ProxyBidService;
import com.mazadak.auctions.util.BidValidator;
import com.mazadak.auctions.util.ProxyBidUpsertResult;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

// TODO: authentication & authorization
@Service
@AllArgsConstructor
public class ProxyBidServiceImpl implements ProxyBidService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final ProxyBidRepository proxyBidRepository;
    private final BidValidator bidValidator;

    @Override
    @Transactional
    public ProxyBidUpsertResult upsertProxyBid(ProxyBidRequest request, UUID auctionId, UUID bidderId) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(
                () -> new ResourceNotFoundException("Auction", "Id", auctionId.toString())
        );
        bidValidator.validateBid(auction, request.getMaxAmount(), bidderId);

        Optional<ProxyBid> optionalExistingBid = proxyBidRepository.findByAuctionIdAndBidderId(auctionId, bidderId);
        if (optionalExistingBid.isPresent()) {
            ProxyBid existingBid = optionalExistingBid.get();
            existingBid.setMaxAmount(request.getMaxAmount());
            proxyBidRepository.save(existingBid);

            if (!Objects.equals(auction.getHighestBidPlaced().getBidderId(), existingBid.getBidderId())) {
                triggerProxyBidding(auction);
            }

            return new ProxyBidUpsertResult(ProxyBidMapper.toResponseDto(existingBid), false);
        }

        ProxyBid proxyBid = ProxyBidMapper.toEntity(request, auctionId, bidderId);
        proxyBidRepository.save(proxyBid);
        if (!Objects.equals(auction.getHighestBidPlaced().getBidderId(), proxyBid.getBidderId())) {
            triggerProxyBidding(auction);
        }

        return new ProxyBidUpsertResult(ProxyBidMapper.toResponseDto(proxyBid), true);
    }

    @Override
    public ProxyBidResponse getProxyBid(UUID auctionId, UUID bidderId) {
        ProxyBid proxyBid = proxyBidRepository.findByAuctionIdAndBidderId(auctionId, bidderId).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Proxy Bid not found for auction id = %s and bidder id = %s", auctionId.toString(), bidderId.toString()))
        );

        return ProxyBidMapper.toResponseDto(proxyBid);
    }

    @Override
    public void deleteProxyBid(UUID auctionId, UUID bidderId) {
        ProxyBid proxyBid = proxyBidRepository.findByAuctionIdAndBidderId(auctionId, bidderId).orElseThrow(
                () -> new ResourceNotFoundException(String.format("Proxy Bid not found for auction id = %s and bidder id = %s", auctionId, bidderId))
        );

        proxyBidRepository.deleteById(proxyBid.getId());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void triggerProxyBidding(Auction auction) {
        List<ProxyBid> proxyBids = proxyBidRepository.findAllByAuctionId(auction.getId());
        if (proxyBids.isEmpty()) {
            return;
        }

        Bid currentHighestBid = auction.getHighestBidPlaced();
        BigDecimal currentHighestAmount = (currentHighestBid != null ? currentHighestBid.getAmount() : auction.getStartingPrice());

        List<ProxyBid> eligibleProxyBids = proxyBidRepository.findTopEligibleProxyBids(
                auction.getId(),
                currentHighestAmount,
                PageRequest.of(0, 2)
        );

        if (eligibleProxyBids.isEmpty()) {
            return;
        }

        Bid newWinningProxyBid = getNewWinningBid(
                auction,
                eligibleProxyBids,
                currentHighestAmount
        );

        if (newWinningProxyBid == null) {
            return;
        }

        bidRepository.save(newWinningProxyBid);
        auction.setHighestBidPlaced(newWinningProxyBid);
        auctionRepository.save(auction);
    }

    private Bid getNewWinningBid(Auction auction, List<ProxyBid> eligibleProxyBids,
                                 BigDecimal currentHighestAmount) {
        ProxyBid highestProxyBid = eligibleProxyBids.get(0);
        ProxyBid secondHighestProxyBid = eligibleProxyBids.size() > 1 ? eligibleProxyBids.get(1) : null;

        BigDecimal winningProxyBidAmount = calculateWinningProxyBidAmount(
                auction,
                highestProxyBid,
                secondHighestProxyBid,
                currentHighestAmount
        );

        if (winningProxyBidAmount == null || winningProxyBidAmount.compareTo(currentHighestAmount) <= 0) {
            return null;
        }

        String idempotencyKey = String.format(
                "proxy-%s-%s-%s",
                auction.getId(),
                highestProxyBid.getBidderId(),
                winningProxyBidAmount
        );

        return new Bid(
                auction.getId(),
                highestProxyBid.getBidderId(),
                winningProxyBidAmount,
                idempotencyKey
        );
    }

    private BigDecimal calculateWinningProxyBidAmount(Auction auction,
                                                      ProxyBid highestProxyBid,
                                                      ProxyBid secondHighestProxyBid,
                                                      BigDecimal currentHighestAmount) {
        BigDecimal minBidIncrement = auction.getBidIncrement();
        BigDecimal highestMax = highestProxyBid.getMaxAmount();
        BigDecimal minValidAmount = currentHighestAmount.add(minBidIncrement);

        // Both proxy bids can't bid
        if (highestMax.compareTo(minValidAmount) < 0) {
            return null;
        }

        // Second-highest proxy bid doesn't exist or can't bid
        if (secondHighestProxyBid == null || secondHighestProxyBid.getMaxAmount().compareTo(minValidAmount) <= 0) {
            return minValidAmount;
        }

        // Second-highest proxy can bid, highest proxy bid wins with the same amount because it was placed earlier.
        return secondHighestProxyBid.getMaxAmount();
    }
}

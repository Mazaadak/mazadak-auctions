package com.mazadak.auctions.service.impl;


import com.mazadak.auctions.dto.request.ProxyBidRequest;
import com.mazadak.auctions.dto.response.ProxyBidResponse;
import com.mazadak.auctions.exception.ResourceNotFoundException;
import com.mazadak.auctions.mapper.ProxyBidMapper;
import com.mazadak.auctions.model.entity.Auction;
import com.mazadak.auctions.model.entity.ProxyBid;
import com.mazadak.auctions.repository.AuctionRepository;
import com.mazadak.auctions.repository.ProxyBidRepository;
import com.mazadak.auctions.service.ProxyBidService;
import com.mazadak.auctions.util.BidValidator;
import com.mazadak.auctions.util.UpsertResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProxyBidServiceImpl implements ProxyBidService {

    private final AuctionRepository auctionRepository;
    private final ProxyBidRepository proxyBidRepository;
    private final BidValidator bidValidator;

    @Override
    @Transactional
    public UpsertResult upsertProxyBid(ProxyBidRequest request, UUID auctionId, UUID bidderId) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow(
                () -> new ResourceNotFoundException("Auction", "Id", auctionId.toString())
        );

        bidValidator.validateAuctionStatus(auction);
        bidValidator.validateSellerIsNotBidder(auction, bidderId);
        bidValidator.validateAuctionTimeWindow(auction);
        bidValidator.validateMinimumBid(auction, request.getMaxAmount());

        Optional<ProxyBid> optionalExistingBid = proxyBidRepository.findByAuctionIdAndBidderId(auctionId, bidderId);
        if (optionalExistingBid.isPresent()) {
            ProxyBid existingBid = optionalExistingBid.get();
            existingBid.setMaxAmount(request.getMaxAmount());
            return new UpsertResult(ProxyBidMapper.toResponseDto(existingBid), false);
        }

        ProxyBid proxyBid = ProxyBidMapper.toEntity(request, auctionId, bidderId);
        proxyBidRepository.save(proxyBid);

        return new UpsertResult(ProxyBidMapper.toResponseDto(proxyBid), true);
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
                () -> new ResourceNotFoundException(String.format("Proxy Bid not found for auction id = %s and bidder id = %s", auctionId.toString(), bidderId.toString()))
        );

        proxyBidRepository.deleteByAuctionIdAndBidderId(auctionId, bidderId);
    }
}

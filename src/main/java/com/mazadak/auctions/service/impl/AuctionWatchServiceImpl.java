package com.mazadak.auctions.service.impl;

import com.mazadak.auctions.dto.response.AuctionWatchResponse;
import com.mazadak.auctions.mapper.AuctionWatchMapper;
import com.mazadak.auctions.repository.AuctionWatchRepository;
import com.mazadak.auctions.service.AuctionWatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuctionWatchServiceImpl implements AuctionWatchService {
    private final AuctionWatchRepository auctionWatchRepository;

    @Override
    public List<AuctionWatchResponse> getById(UUID auctionId) {
        return auctionWatchRepository.findAllByAuction_Id(auctionId)
                .stream()
                .map(AuctionWatchMapper::toResponse)
                .toList();
    }
}

package com.mazadak.auctions.service;

import com.mazadak.auctions.dto.request.AuctionFilterDto;
import com.mazadak.auctions.dto.request.CreateAuctionRequest;
import com.mazadak.auctions.dto.request.UpdateAuctionRequest;
import com.mazadak.auctions.dto.response.AuctionResponse;
import com.mazadak.auctions.dto.response.AuctionWatchResponse;
import com.mazadak.auctions.model.entity.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;


public interface AuctionService {
    // CRUD
    AuctionResponse findAuctionById(UUID id);
    Page<AuctionResponse> findAuctionsByCriteria(AuctionFilterDto filter, Pageable pageable);
    AuctionResponse createAuction(UUID idempotencyKey, CreateAuctionRequest dto);
    AuctionResponse updateAuction(UUID id, UpdateAuctionRequest request);
    void deleteById(UUID id);

    // STATUS
    AuctionResponse cancelAuction(UUID id);
    AuctionResponse pauseAuction(UUID id);
    AuctionResponse resumeAuction(UUID id);

    // WATCH
    void addWatcher(UUID id, UUID userId);
    void removeWatcher(UUID id, UUID userId);
    List<UUID> getWatcherIds(UUID id);
    List<AuctionWatchResponse> getWatchlist(UUID userId);

    Boolean isUserWatchingAuction(UUID userId, UUID auctionId);

    Boolean existsByProductId(UUID productId);
    void restoreAuction(UUID auctionId);

    void assertUserOwnsAuction(UUID userId, Auction auction);

    AuctionResponse findListedAuctionByProductId(UUID productId);
}

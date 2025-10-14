package com.mazadak.auctions.service;

import com.mazadak.auctions.dto.request.AuctionFilterDto;
import com.mazadak.auctions.dto.request.CreateAuctionRequest;
import com.mazadak.auctions.dto.request.UpdateAuctionRequest;
import com.mazadak.auctions.dto.response.AuctionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface AuctionService {
    // CRUD
    AuctionResponse findAuctionById(Long id);
    Page<AuctionResponse> findAuctionsByCriteria(AuctionFilterDto filter, Pageable pageable);
    AuctionResponse createAuction(CreateAuctionRequest dto);
    AuctionResponse updateAuction(Long id, UpdateAuctionRequest request);
    void deleteById(Long id);

    // STATUS
    AuctionResponse cancelAuction(Long id);
    AuctionResponse pauseAuction(Long id);
    AuctionResponse resumeAuction(Long id);

    // WATCH
    void addWatcher(Long id, Long userId);
    void removeWatcher(Long id, Long userId);
    List<Long> getWatcherIds(Long id);
}

package com.mazadak.auctions.service;

import com.mazadak.auctions.dto.AuctionDto;
import com.mazadak.auctions.dto.AuctionFilterDto;
import com.mazadak.auctions.dto.request.CreateAuctionRequest;
import com.mazadak.auctions.model.entity.Auction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AuctionService {
    AuctionDto findAuctionById(Long id);
    Page<AuctionDto> findAuctionsByCriteria(AuctionFilterDto filter, Pageable pageable);
    Auction createAuction(CreateAuctionRequest dto);
}

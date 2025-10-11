package com.mazadak.auctions.service.impl;

import com.mazadak.auctions.dto.AuctionDto;
import com.mazadak.auctions.dto.AuctionFilterDto;
import com.mazadak.auctions.dto.request.CreateAuctionRequest;
import com.mazadak.auctions.exception.ResourceNotFoundException;
import com.mazadak.auctions.mapper.AuctionMapper;
import com.mazadak.auctions.model.entity.Auction;
import com.mazadak.auctions.repository.AuctionRepository;
import com.mazadak.auctions.repository.specification.AuctionSpecifications;
import com.mazadak.auctions.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {
    private final AuctionRepository auctionRepository;

    @Override
    public AuctionDto findAuctionById(Long id) {
        var auction = auctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auction", "Id", id.toString()));

        return AuctionMapper.toDto(auction);
    }

    @Override
    public Page<AuctionDto> findAuctionsByCriteria(AuctionFilterDto filter, Pageable pageable) {
        Specification<Auction> specification = AuctionSpecifications.buildFromFilter(filter);
        return auctionRepository.findAll(specification, pageable)
                .map(AuctionMapper::toDto);
    }

    @Override
    public Auction createAuction(CreateAuctionRequest dto) {
        return auctionRepository.save(AuctionMapper.toEntity(dto));
    }
}

package com.mazadak.auctions.service.impl;

import com.mazadak.auctions.dto.request.AuctionFilterDto;
import com.mazadak.auctions.dto.request.CreateAuctionRequest;
import com.mazadak.auctions.dto.request.UpdateAuctionRequest;
import com.mazadak.auctions.dto.response.AuctionResponse;
import com.mazadak.auctions.exception.InvalidAuctionOperationException;
import com.mazadak.auctions.exception.ResourceNotFoundException;
import com.mazadak.auctions.mapper.AuctionMapper;
import com.mazadak.auctions.model.entity.Auction;
import com.mazadak.auctions.model.enumeration.AuctionStatus;
import com.mazadak.auctions.repository.AuctionRepository;
import com.mazadak.auctions.repository.specification.AuctionSpecifications;
import com.mazadak.auctions.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {
    private final AuctionRepository auctionRepository;

    @Override
    public AuctionResponse findAuctionById(Long id) {
        var auction = auctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auction", "Id", id.toString()));

        return AuctionMapper.toResponseDto(auction);
    }

    @Override
    public Page<AuctionResponse> findAuctionsByCriteria(AuctionFilterDto filter, Pageable pageable) {
        Specification<Auction> specification = AuctionSpecifications.buildFromFilter(filter);
        return auctionRepository.findAll(specification, pageable)
                .map(AuctionMapper::toResponseDto);
    }

    @Override
    public AuctionResponse createAuction(CreateAuctionRequest dto) {
        var saved = auctionRepository.save(AuctionMapper.toEntity(dto));
        return AuctionMapper.toResponseDto(saved);
    }

    @Override
    public AuctionResponse updateAuction(Long id, UpdateAuctionRequest request) {
        var auction = auctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auction", "id", id.toString()));

        if (isAuctionActive(auction)) {
            throw new InvalidAuctionOperationException("Cannot update an auction that's already active", auction.getId());
        }

        auction.setProductId(request.productId());
        auction.setTitle(request.title());
        auction.setStartingPrice(request.startingPrice());
        auction.setReservePrice(request.reservePrice());
        auction.setBidIncrement(request.bidIncrement());
        auction.setStartTime(request.startTime());
        auction.setStatus(auction.getStartTime().isAfter(LocalDateTime.now()) ? AuctionStatus.SCHEDULED : AuctionStatus.STARTED);
        auction.setEndTime(request.endTime());

        auctionRepository.save(auction);

        return AuctionMapper.toResponseDto(auction);
    }

    private boolean isAuctionActive(Auction auction) {
        return auction.getStatus() == AuctionStatus.ACTIVE;
    }

    @Override
    public void deleteById(Long id) {
        var auction = auctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auction", "id", id.toString()));

        if (isAuctionActive(auction)) {
            throw new InvalidAuctionOperationException("Cannot update an auction that's already active", auction.getId());
        }

        auctionRepository.delete(auction);
    }
}

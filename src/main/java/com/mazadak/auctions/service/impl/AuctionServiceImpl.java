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
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.core.support.RepositoryMethodInvocationListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {
    private final AuctionRepository auctionRepository;
    private final RepositoryMethodInvocationListener repositoryMethodInvocationListener;

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

        if (EnumSet.of(AuctionStatus.ENDED, AuctionStatus.CANCELLED).contains(auction.getStatus())) {
            throw new InvalidAuctionOperationException("Cannot update an auction that has been cancelled or ended", auction.getId());
        }

        auction.setProductId(request.productId());
        auction.setTitle(request.title());
        auction.setStartingPrice(request.startingPrice());
        auction.setReservePrice(request.reservePrice());
        auction.setBidIncrement(request.bidIncrement());
        updateStartTime(auction, request.startTime());
        updateEndTime(auction, request.endTime());

        auctionRepository.save(auction);

        return AuctionMapper.toResponseDto(auction);
    }

    private boolean isAuctionActive(Auction auction) {
        return auction.getStatus() == AuctionStatus.ACTIVE;
    }

    private void updateStartTime(Auction auction, LocalDateTime newStartTime) {
        if (auction.getStatus() == AuctionStatus.SCHEDULED) {
            if (newStartTime.isBefore(LocalDateTime.now())) {
                throw new InvalidAuctionOperationException("Start time cannot be in the past.", auction.getId());
            }
            auction.setStartTime(newStartTime);
        } else {
            throw new InvalidAuctionOperationException("Cannot modify auction start time after it has started or ended", auction.getId());
        }
    }

    private void updateEndTime(Auction auction, LocalDateTime newEndTime) {
        switch (auction.getStatus()) {
            case STARTED, ACTIVE, PAUSED -> {
                if (newEndTime.isBefore(auction.getEndTime())) {
                    throw new InvalidAuctionOperationException("Cannot shorten auction duration after it has started.", auction.getId());
                }
            }
            case ENDED, CANCELLED -> {
                throw new InvalidAuctionOperationException("Cannot modify an auction that has ended or been cancelled.", auction.getId());
            }
        }
        auction.setEndTime(newEndTime);
    }

    @Override
    public void deleteById(Long id) {
        var auction = auctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auction", "id", id.toString()));

        if (isAuctionActive(auction)) {
            throw new InvalidAuctionOperationException("Cannot update an auction that is already active", auction.getId());
        }

        auctionRepository.delete(auction);
    }

    @Override
    public AuctionResponse cancelAuction(Long id) {
        var auction = auctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auction", "id", id.toString()));

        if (isAuctionActive(auction)) {
            throw new InvalidAuctionOperationException("Cannot cancel an active auction.", id);
        }

        if (auction.getStatus() == AuctionStatus.ENDED) {
            throw new InvalidAuctionOperationException("Cannot cancel an ended auction.", id);
        }

        auction.setStatus(AuctionStatus.CANCELLED);
        return AuctionMapper.toResponseDto(auctionRepository.save(auction));
    }

    @Override
    public AuctionResponse pauseAuction(Long id) {
        var auction = auctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auction", "id", id.toString()));

        if (isAuctionActive(auction)) {
            throw new InvalidAuctionOperationException("Cannot pause an active auction.", id);
        }

        if (auction.getStatus() != AuctionStatus.STARTED) {
            throw new InvalidAuctionOperationException("Cannot pause an active that is not started.", id);
        }

        auction.setStatus(AuctionStatus.PAUSED);
        return AuctionMapper.toResponseDto(auctionRepository.save(auction));
    }

    @Override
    public AuctionResponse resumeAuction(Long id) {
        var auction = auctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auction", "id", id.toString()));

        if (auction.getStatus() != AuctionStatus.PAUSED) {
            throw new InvalidAuctionOperationException("Cannot resume an auction that is not paused.", id);
        }

        auction.setStatus(AuctionStatus.STARTED);
        return AuctionMapper.toResponseDto(auctionRepository.save(auction));
    }
}

package com.mazadak.auctions.service.impl;

import com.mazadak.auctions.dto.request.AuctionFilterDto;
import com.mazadak.auctions.dto.request.CreateAuctionRequest;
import com.mazadak.auctions.dto.request.UpdateAuctionRequest;
import com.mazadak.auctions.dto.response.AuctionResponse;
import com.mazadak.auctions.dto.response.AuctionWatchResponse;
import com.mazadak.auctions.exception.*;
import com.mazadak.auctions.mapper.AuctionMapper;
import com.mazadak.auctions.mapper.AuctionWatchMapper;
import com.mazadak.auctions.model.entity.Auction;
import com.mazadak.auctions.model.entity.AuctionWatch;
import com.mazadak.auctions.model.entity.IdempotencyRecord;
import com.mazadak.auctions.model.enumeration.AuctionStatus;
import com.mazadak.auctions.repository.AuctionRepository;
import com.mazadak.auctions.repository.AuctionWatchRepository;
import com.mazadak.auctions.repository.IdempotencyRepository;
import com.mazadak.auctions.repository.specification.AuctionSpecifications;
import com.mazadak.auctions.service.AuctionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {
    private final AuctionRepository auctionRepository;
    private final AuctionWatchRepository auctionWatchRepository;
    private final IdempotencyRepository idempotencyRepository;

    @Override
    public AuctionResponse findAuctionById(UUID id) {
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
    @Transactional
    public AuctionResponse createAuction(UUID idempotencyKey, CreateAuctionRequest dto) {
        try {
            var idempotency = new IdempotencyRecord(idempotencyKey, false);
            idempotencyRepository.save(idempotency);

            if (auctionRepository.listedAuctionExistsForProduct(dto.productId())) {
                throw new ProductAlreadyHasAListedAuctionException("An auction is already listed for product " + dto.productId());
            }

            var saved = auctionRepository.save(AuctionMapper.toEntity(dto));
            idempotency.setProcessed(true);

            return AuctionMapper.toResponseDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new IdempotencyKeyAlreadyExistsException(idempotencyKey, "Auction", dto);
        }
    }

    @Override
    public AuctionResponse updateAuction(UUID id, UpdateAuctionRequest request) {
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
    public void deleteById(UUID id) {
        var auction = auctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auction", "id", id.toString()));

        if (isAuctionActive(auction)) {
            throw new InvalidAuctionOperationException("Cannot update an auction that is already active", auction.getId());
        }

        auction.setDeleted(true);
        auctionRepository.save(auction);
    }

    @Override
    public AuctionResponse cancelAuction(UUID id) {
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
    public AuctionResponse pauseAuction(UUID id) {
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
    public AuctionResponse resumeAuction(UUID id) {
        var auction = auctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auction", "id", id.toString()));

        if (auction.getStatus() != AuctionStatus.PAUSED) {
            throw new InvalidAuctionOperationException("Cannot resume an auction that is not paused.", id);
        }

        auction.setStatus(AuctionStatus.STARTED);
        return AuctionMapper.toResponseDto(auctionRepository.save(auction));
    }

    @Override
    public void addWatcher(UUID id, UUID userId) {
        var auction = auctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auction", "id", id.toString()));

        var watch = new AuctionWatch(auction, userId, false);

        auctionWatchRepository.save(watch);
    }

    @Override
    public void removeWatcher(UUID id, UUID userId) {
        var watch = auctionWatchRepository.findAuctionWatchByUserIdAndAuction_Id(userId, id)
                .orElseThrow(() -> new ResourceNotFoundException("AuctionWatch", "userId, id", userId + ", " + id));

        auctionWatchRepository.delete(watch);
    }

    @Override
    public List<Long> getWatcherIds(UUID id) {
        return auctionWatchRepository.findAllByAuction_Id(id)
                .stream()
                .toList();
    }

    @Override
    public List<AuctionWatchResponse> getWatchlist(UUID userId) {
        return auctionWatchRepository.findAllByUserId(userId)
                .stream()
                .map(AuctionWatchMapper::toResponse)
                .toList();
    }

    @Override
    public Boolean existsByProductId(UUID productId) {
        return auctionRepository.existsByProductId(productId);
    }

    @Override
    public void restoreAuction(UUID auctionId) {
        var auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException("Auction", "id", auctionId.toString()));

        auction.setDeleted(false);
        auctionRepository.save(auction);
    }

    @Override
    public void assertUserOwnsAuction(UUID userId, Auction auction) {
        if (!Objects.equals(userId, auction.getSellerId())) {
            throw new UnauthorizedException(
                    "User does not own this auction. Expected sellerId=%s but found %s"
                            .formatted(auction.getSellerId(), userId)
            );
        }
    }

    @Override
    public AuctionResponse findListedAuctionByProductId(UUID productId) {
        var auction = auctionRepository.findAuctionByProductIdAndDeletedFalseAndStatusIn(productId,
                List.of(AuctionStatus.SCHEDULED,
                        AuctionStatus.STARTED,
                        AuctionStatus.PAUSED,
                        AuctionStatus.ACTIVE)
        ).orElseThrow(() -> new ResourceNotFoundException("Auction", "productId", productId.toString()));

        return AuctionMapper.toResponseDto(auction);
    }

    @Override
    public Boolean isUserWatchingAuction(UUID userId, UUID auctionId) {
        return auctionWatchRepository.existsByUserIdAndId(userId, auctionId);
    }
}

package com.mazadak.auctions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mazadak.auctions.dto.request.CreateAuctionRequest;
import com.mazadak.auctions.dto.request.UpdateAuctionRequest;
import com.mazadak.auctions.dto.response.AuctionResponse;
import com.mazadak.auctions.model.entity.Auction;
import com.mazadak.auctions.model.entity.AuctionWatch;
import com.mazadak.auctions.model.entity.IdempotencyRecord;
import com.mazadak.auctions.model.entity.OutboxEvent;
import com.mazadak.auctions.model.enumeration.AuctionStatus;
import com.mazadak.auctions.repository.AuctionRepository;
import com.mazadak.auctions.repository.AuctionWatchRepository;
import com.mazadak.auctions.repository.IdempotencyRepository;
import com.mazadak.auctions.repository.OutboxEventRepository;
import com.mazadak.auctions.service.impl.AuctionServiceImpl;
import com.mazadak.common.exception.domain.auction.ProductAlreadyHasAListedAuctionException;
import com.mazadak.common.exception.shared.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuctionServiceImplTest {

    @Mock
    private AuctionRepository auctionRepository;
    @Mock
    private AuctionWatchRepository auctionWatchRepository;
    @Mock
    private IdempotencyRepository idempotencyRepository;
    @Mock
    private OutboxEventRepository outboxEventRepository;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuctionServiceImpl auctionService;

    private UUID auctionId;
    private UUID userId;
    private Auction auction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        auctionId = UUID.randomUUID();
        userId = UUID.randomUUID();

        auction = new Auction();
        auction.setId(auctionId);
        auction.setSellerId(userId);
        auction.setProductId(UUID.randomUUID());
        auction.setTitle("Sample Auction");
        auction.setStartingPrice(BigDecimal.valueOf(100));
        auction.setReservePrice(BigDecimal.valueOf(150));
        auction.setBidIncrement(BigDecimal.valueOf(10));
        auction.setStartTime(LocalDateTime.now().plusDays(1));
        auction.setEndTime(LocalDateTime.now().plusDays(2));
        auction.setStatus(AuctionStatus.SCHEDULED);
        auction.setDeleted(false);
    }

    @Test
    void findAuctionById_ShouldReturnAuctionResponse() {
        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        AuctionResponse response = auctionService.findAuctionById(auctionId);
        assertNotNull(response);
        verify(auctionRepository).findById(auctionId);
    }

    @Test
    void findAuctionById_ShouldThrowResourceNotFound() {
        when(auctionRepository.findById(auctionId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> auctionService.findAuctionById(auctionId));
    }

    @Test
    void createAuction_ShouldReturnAuctionResponse_WhenNoIdempotency() {
        CreateAuctionRequest request = new CreateAuctionRequest(
                UUID.randomUUID(),
                userId,
                "Title",
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(150),
                BigDecimal.valueOf(10),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );
        UUID idempotencyKey = UUID.randomUUID();
        when(idempotencyRepository.findById(idempotencyKey)).thenReturn(Optional.empty());
        when(auctionRepository.listedAuctionExistsForProduct(request.productId())).thenReturn(false);
        when(auctionRepository.save(any())).thenReturn(auction);

        var response = auctionService.createAuction(idempotencyKey, request);
        assertNotNull(response);
        verify(idempotencyRepository).save(any(IdempotencyRecord.class));
        verify(auctionRepository).save(any(Auction.class));
    }

    @Test
    void createAuction_ShouldThrowProductAlreadyHasListedAuctionException() {
        CreateAuctionRequest request = new CreateAuctionRequest(
                UUID.randomUUID(),
                userId,
                "Title",
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(150),
                BigDecimal.valueOf(10),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );
        UUID idempotencyKey = UUID.randomUUID();
        when(idempotencyRepository.findById(idempotencyKey)).thenReturn(Optional.empty());
        when(auctionRepository.listedAuctionExistsForProduct(request.productId())).thenReturn(true);

        assertThrows(ProductAlreadyHasAListedAuctionException.class, () -> auctionService.createAuction(idempotencyKey, request));
    }

    @Test
    void updateAuction_ShouldUpdateFields() {
        UpdateAuctionRequest request = new UpdateAuctionRequest(
                auction.getProductId(),
                "New Title",
                BigDecimal.valueOf(200),
                BigDecimal.valueOf(250),
                BigDecimal.valueOf(20),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3)
        );
        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));

        AuctionResponse response = auctionService.updateAuction(auctionId, request);
        assertEquals("New Title", response.title());
        verify(auctionRepository).save(auction);
    }

    @Test
    void deleteById_ShouldSetDeletedTrueAndSaveOutbox() throws Exception {
        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        auctionService.deleteById(auctionId);
        assertTrue(auction.isDeleted());
        verify(outboxEventRepository).save(any(OutboxEvent.class));
        verify(auctionRepository).save(auction);
    }

    @Test
    void addWatcher_ShouldSaveAuctionWatch() {
        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        auctionService.addWatcher(auctionId, userId);
        verify(auctionWatchRepository).save(any(AuctionWatch.class));
    }

    @Test
    void removeWatcher_ShouldDeleteAuctionWatch() {
        AuctionWatch watch = new AuctionWatch(auction, userId, false);
        when(auctionWatchRepository.findAuctionWatchByUserIdAndAuction_Id(userId, auctionId)).thenReturn(Optional.of(watch));
        auctionService.removeWatcher(auctionId, userId);
        verify(auctionWatchRepository).delete(watch);
    }

    @Test
    void getWatcherIds_ShouldReturnUserIds() {
        AuctionWatch watch = new AuctionWatch(auction, userId, false);
        when(auctionWatchRepository.findAllByAuction_Id(auctionId)).thenReturn(List.of(watch));
        List<UUID> ids = auctionService.getWatcherIds(auctionId);
        assertEquals(1, ids.size());
        assertEquals(userId, ids.get(0));
    }

    @Test
    void isUserWatchingAuction_ShouldReturnBoolean() {
        when(auctionWatchRepository.existsByUserIdAndId(userId, auctionId)).thenReturn(true);
        assertTrue(auctionService.isUserWatchingAuction(userId, auctionId));
    }
}

package com.mazadak.auctions;

import com.mazadak.auctions.dto.request.PlaceBidRequest;
import com.mazadak.auctions.dto.response.BidResponse;
import com.mazadak.auctions.model.entity.Auction;
import com.mazadak.auctions.model.entity.Bid;
import com.mazadak.auctions.model.enumeration.AuctionStatus;
import com.mazadak.auctions.repository.AuctionRepository;
import com.mazadak.auctions.repository.BidRepository;
import com.mazadak.auctions.service.ProxyBidService;
import com.mazadak.auctions.service.impl.BidServiceImpl;
import com.mazadak.auctions.util.BidValidator;
import com.mazadak.common.exception.shared.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BidServiceImplTest {

    @Mock
    private AuctionRepository auctionRepository;
    @Mock
    private BidRepository bidRepository;
    @Mock
    private BidValidator bidValidator;
    @Mock
    private ProxyBidService proxyBidService;

    @InjectMocks
    private BidServiceImpl bidService;

    private UUID auctionId;
    private UUID bidderId;
    private Auction auction;
    private Bid bid;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        auctionId = UUID.randomUUID();
        bidderId = UUID.randomUUID();

        auction = new Auction();
        auction.setId(auctionId);
        auction.setStatus(AuctionStatus.STARTED);
        auction.setHighestBidPlaced(null);
        auction.setProductId(UUID.randomUUID());

        bid = new Bid();
        bid.setId(UUID.randomUUID());
        bid.setAuctionId(auctionId);
        bid.setBidderId(bidderId);
        bid.setAmount(BigDecimal.valueOf(100));
    }

    @Test
    void placeBid_ShouldReturnBidResponse_WhenNoExistingBid() {
        PlaceBidRequest request = new PlaceBidRequest(bidderId, BigDecimal.valueOf(100));

        when(bidRepository.findByIdempotencyKey(anyString())).thenReturn(Optional.empty());
        when(auctionRepository.findByIdForUpdate(auctionId)).thenReturn(Optional.of(auction));
        when(bidRepository.countByAuctionId(auctionId)).thenReturn(0L);
        when(bidRepository.save(any(Bid.class))).thenReturn(bid);
        when(auctionRepository.save(any(Auction.class))).thenReturn(auction);

        BidResponse response = bidService.placeBid(request, auctionId, "idem-key");

        assertNotNull(response);
        verify(proxyBidService).triggerProxyBidding(auction);
        verify(bidRepository).save(any(Bid.class));
        verify(auctionRepository).save(any(Auction.class));
    }

    @Test
    void placeBid_ShouldReturnExistingBid_WhenIdempotencyHit() {
        when(bidRepository.findByIdempotencyKey("idem-key")).thenReturn(Optional.of(bid));

        PlaceBidRequest request = new PlaceBidRequest(bidderId, BigDecimal.valueOf(100));
        BidResponse response = bidService.placeBid(request, auctionId, "idem-key");

        assertNotNull(response);
        assertEquals(bid.getId(), response.id());
        verifyNoInteractions(proxyBidService);
    }

    @Test
    void getHighestBid_ShouldReturnBidResponse() {
        auction.setHighestBidPlaced(bid);
        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));

        BidResponse response = bidService.getHighestBid(auctionId);

        assertNotNull(response);
        assertEquals(bid.getId(), response.id());
    }

    @Test
    void getHighestBid_ShouldThrowResourceNotFound() {
        when(auctionRepository.findById(auctionId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> bidService.getHighestBid(auctionId));
    }

    @Test
    void getBids_ShouldReturnPageOfBidResponses() {
        Page<Bid> page = new PageImpl<>(List.of(bid));
        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(auction));
        when(bidRepository.findByAuctionId(auctionId, Pageable.unpaged())).thenReturn(page);

        Page<BidResponse> responses = bidService.getBids(auctionId, null, Pageable.unpaged());

        assertEquals(1, responses.getTotalElements());
    }

    @Test
    void getBidsByBidder_ShouldReturnPageOfBidResponses() {
        Page<Bid> page = new PageImpl<>(List.of(bid));
        when(bidRepository.findByBidderId(bidderId, Pageable.unpaged())).thenReturn(page);

        Page<BidResponse> responses = bidService.getBidsByBidder(bidderId, Pageable.unpaged());

        assertEquals(1, responses.getTotalElements());
    }

    @Test
    void getHighestBidForEachBidderAboveReservePrice_ShouldReturnList() {
        when(bidRepository.findHighestBidsPerBidderByAuctionIdAboveReservePrice(auctionId))
                .thenReturn(List.of(bid));

        var result = bidService.getHighestBidForEachBidderAboveReservePrice(auctionId);

        assertEquals(1, result.size());
    }
}

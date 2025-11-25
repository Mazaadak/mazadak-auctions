package com.mazadak.auctions;


import com.mazadak.auctions.dto.response.AuctionWatchResponse;
import com.mazadak.auctions.model.entity.Auction;
import com.mazadak.auctions.model.entity.AuctionWatch;
import com.mazadak.auctions.repository.AuctionWatchRepository;
import com.mazadak.auctions.service.impl.AuctionWatchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuctionWatchServiceImplTest {

    @Mock
    private AuctionWatchRepository auctionWatchRepository;

    @InjectMocks
    private AuctionWatchServiceImpl auctionWatchService;

    private UUID auctionId;
    private UUID userId;
    private Auction auction;
    private AuctionWatch watch;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        auctionId = UUID.randomUUID();
        userId = UUID.randomUUID();

        auction = new Auction();
        auction.setId(auctionId);

        watch = new AuctionWatch(auction, userId, false);
    }

    @Test
    void getById_ShouldReturnListOfAuctionWatchResponse() {
        when(auctionWatchRepository.findAllByAuction_Id(auctionId)).thenReturn(List.of(watch));

        List<AuctionWatchResponse> responses = auctionWatchService.getById(auctionId);

        assertEquals(1, responses.size());
        assertEquals(userId, responses.get(0).userId());
        verify(auctionWatchRepository).findAllByAuction_Id(auctionId);
    }

    @Test
    void getById_ShouldReturnEmptyList_WhenNoWatchers() {
        when(auctionWatchRepository.findAllByAuction_Id(auctionId)).thenReturn(List.of());

        List<AuctionWatchResponse> responses = auctionWatchService.getById(auctionId);

        assertEquals(0, responses.size());
        verify(auctionWatchRepository).findAllByAuction_Id(auctionId);
    }
}

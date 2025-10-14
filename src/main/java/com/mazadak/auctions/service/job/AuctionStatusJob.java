package com.mazadak.auctions.service.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mazadak.auctions.dto.event.AuctionStartedEvent;
import com.mazadak.auctions.model.entity.Auction;
import com.mazadak.auctions.model.entity.OutboxEvent;
import com.mazadak.auctions.model.enumeration.AuctionStatus;
import com.mazadak.auctions.repository.AuctionRepository;
import com.mazadak.auctions.repository.OutboxEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AuctionStatusJob {
    private final AuctionRepository auctionRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(AuctionStatusJob.class);

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void updateAuctionStatuses() {
        var now = LocalDateTime.now();

        var dueAuctions = auctionRepository.findDueAuctions(now);
        var updatedAuctions = new ArrayList<Auction>();

        logger.info("Processing {} auctions due for status update", dueAuctions.size());

        for (var auction : dueAuctions) {
            try {
                var oldStatus = auction.getStatus();
                handleStatusTransition(auction, now);
                if (auction.getStatus() != oldStatus) {
                    updatedAuctions.add(auction);
                }
            } catch (Exception e) {
                logger.error("Failed to update auction {}", auction.getId(), e);
            }
        }

        if (!updatedAuctions.isEmpty()) {
            auctionRepository.saveAll(updatedAuctions);
        }
    }

    private void handleStatusTransition(Auction auction, LocalDateTime now) {
        switch (auction.getStatus()) {
            case SCHEDULED -> {
                if (auction.getEndTime().isBefore(now) || auction.getEndTime().isEqual(now)) {
                    auction.setStatus(AuctionStatus.ENDED);
                } else if (auction.getStartTime().isBefore(now) || auction.getStartTime().isEqual(now)) {
                    startAuction(auction);
                }
            }
            case STARTED, ACTIVE, PAUSED -> {
                if (auction.getEndTime().isBefore(now) || auction.getEndTime().isEqual(now)) {
                    auction.setStatus(AuctionStatus.ENDED);
                }
            }
            default -> {}
        }
    }

    private void startAuction(Auction auction) {
        auction.setStatus(AuctionStatus.STARTED);

        try {
            var event = new AuctionStartedEvent(auction.getId(), auction.getTitle(), auction.getStartTime());
            var outboxEvent = new OutboxEvent("Auction",
                    "AuctionStarted",
                    objectMapper.writeValueAsString(event),
                    false
            );
            outboxEventRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize AuctionStartedEvent for auction {}", auction.getId(), e);
        }

    }
}

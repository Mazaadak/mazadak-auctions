package com.mazadak.auctions.service.job;

import com.mazadak.auctions.model.entity.Auction;
import com.mazadak.auctions.model.enumeration.AuctionStatus;
import com.mazadak.auctions.repository.AuctionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuctionStatusJob {
    private final AuctionRepository auctionRepository;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void updateAuctionStatuses() {
        var now = LocalDateTime.now();

        var dueAuctions = auctionRepository.findDueAuctions(now);

        for (var auction : dueAuctions) {
            var oldStatus = auction.getStatus();
            handleStatusTransition(auction, now);
            if (auction.getStatus() != oldStatus) {
                auctionRepository.save(auction);
            }
        }
    }

    private void handleStatusTransition(Auction auction, LocalDateTime now) {
        switch (auction.getStatus()) {
            case SCHEDULED -> {
                if (auction.getEndTime().isBefore(now) || auction.getEndTime().isEqual(now)) {
                    auction.setStatus(AuctionStatus.ENDED);
                } else if (auction.getStartTime().isBefore(now) || auction.getStartTime().isEqual(now)) {
                    auction.setStatus(AuctionStatus.STARTED);
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
}

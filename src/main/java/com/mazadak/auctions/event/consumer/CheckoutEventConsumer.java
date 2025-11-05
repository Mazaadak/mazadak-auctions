package com.mazadak.auctions.event.consumer;

import com.mazadak.auctions.dto.event.AuctionCompletedEvent;
import com.mazadak.auctions.dto.event.AuctionInvalidEvent;
import com.mazadak.auctions.model.enumeration.AuctionStatus;
import com.mazadak.auctions.service.AuctionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class CheckoutEventConsumer {
    @Bean
    public Consumer<AuctionCompletedEvent> auctionCompleted(AuctionService auctionService) {
        return auctionCompletedEvent -> {
            log.info("Consuming AuctionCompletedEvent {}", auctionCompletedEvent);

            auctionService.setAuctionStatus(auctionCompletedEvent.auctionId(), AuctionStatus.COMPLETED);
            log.info("Set auction {} status to COMPLETED", auctionCompletedEvent.auctionId());
        };
    }

    @Bean
    public Consumer<AuctionInvalidEvent> auctionInvalid(AuctionService auctionService) {
        return auctionInvalidEvent -> {
            log.info("Consuming AuctionInvalidEvent {}", auctionInvalidEvent);

            auctionService.setAuctionStatus(auctionInvalidEvent.auctionId(), AuctionStatus.INVALID);
            log.info("Set auction {} status to INVALID", auctionInvalidEvent.auctionId());
        };
    }
}

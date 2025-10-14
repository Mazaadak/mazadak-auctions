package com.mazadak.auctions.event.publisher;

import com.mazadak.auctions.constant.AuctionMessagingConstants;
import com.mazadak.auctions.model.entity.OutboxEvent;
import com.mazadak.auctions.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxPublisher {
    private final OutboxEventRepository outboxEventRepository;
    private final StreamBridge streamBridge;
    private final Logger logger = LoggerFactory.getLogger(OutboxPublisher.class);

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {
        List<OutboxEvent> events = outboxEventRepository.findByPublishedFalse();

        for (var event : events) {
            try {
                logger.info("Sending outbox event: {}", event);
                streamBridge.send(AuctionMessagingConstants.AUCTION_STARTED_BINDING, event.getPayload());
                event.setPublished(true);
                outboxEventRepository.save(event);
            } catch (Exception e) {
                logger.error("Couldn't publish event {}.", event, e);
            }
        }
    }

}

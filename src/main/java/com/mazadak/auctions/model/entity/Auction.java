package com.mazadak.auctions.model.entity;

import com.mazadak.auctions.exception.InvalidAuctionStatusTransitionException;
import com.mazadak.auctions.model.enumeration.AuctionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "auctions")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Auction extends BaseEntity {
    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private UUID sellerId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private BigDecimal startingPrice;

    private BigDecimal reservePrice = startingPrice;

    @OneToOne
    @JoinColumn(name = "highest_bid_placed_id")
    private Bid highestBidPlaced;

    @Column(nullable = false)
    private BigDecimal bidIncrement;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private AuctionStatus status = AuctionStatus.SCHEDULED;

    @Column(name = "idempotency_key", unique = true)
    private UUID idempotencyKey;

    private boolean deleted = false;

    public void setStatus(AuctionStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new InvalidAuctionStatusTransitionException("Cannot transition auction status from " + this.status + " to " + newStatus, this.status, newStatus);
        }
        this.status = newStatus;
    }
}

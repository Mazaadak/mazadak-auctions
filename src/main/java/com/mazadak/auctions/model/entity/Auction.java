package com.mazadak.auctions.model.entity;

import com.mazadak.auctions.exception.InvalidAuctionStatusTransitionException;
import com.mazadak.auctions.model.enumeration.AuctionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "auctions")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Auction extends BaseEntity {
    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long sellerId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private BigDecimal startingPrice;

    private BigDecimal reservePrice = startingPrice;

    private BigDecimal highestBidPlaced;

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

    public void setStatus(AuctionStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new InvalidAuctionStatusTransitionException("Cannot transition auction status from " + this.status + " to " + newStatus, this.status, newStatus);
        }
        this.status = newStatus;
    }
}

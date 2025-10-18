package com.mazadak.auctions.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "auction_watchlist")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AuctionWatch extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id")
    private Auction auction;

    private UUID userId;

    private boolean notified = false;
}

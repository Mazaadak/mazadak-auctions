package com.mazadak.auctions.model.entity;

import jakarta.persistence.*;
import lombok.*;

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

    private Long userId;

    private boolean notified = false;
}

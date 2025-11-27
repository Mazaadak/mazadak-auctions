package com.mazadak.auctions.model.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/*
CREATE TABLE bids (
  bid_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  auction_id BIGINT NOT NULL,
  bidder_id BIGINT NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  is_auto BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_auction_amount (auction_id, amount DESC, created_at ASC),
  FOREIGN KEY (auction_id) REFERENCES auctions(auction_id)
);
*/


@Entity
@Table(name = "bids", indexes = {
        @Index(name = "idx_auction_amount", columnList = "auction_id, amount DESC, created_at ASC")
})
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class Bid extends BaseEntity {

    @Column(nullable = false)
    private UUID auctionId;

    @Column(nullable = false)
    private UUID bidderId;

    private BigDecimal amount;

    @Column(unique = true)
    private String idempotencyKey;
}

package com.mazadak.auctions.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;


/*
CREATE TABLE proxy_bids (
  proxy_bid_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  auction_id BIGINT NOT NULL,
  bidder_id BIGINT NOT NULL,
  max_amount DECIMAL(18,2) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT uq_proxy_bidder_auction UNIQUE (auction_id, bidder_id),
  FOREIGN KEY (auction_id) REFERENCES auctions(auction_id)
);
*/
@Entity
@Table(name = "proxy_bids", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"auction_id", "bidder_id"})
})
@Getter @Setter @ToString
@AllArgsConstructor @NoArgsConstructor
public class ProxyBid extends BaseEntity {

    @Column(nullable = false)
    private UUID auctionId;

    @Column(nullable = false)
    private UUID bidderId;

    private BigDecimal maxAmount;
}

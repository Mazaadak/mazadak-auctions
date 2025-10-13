package com.mazadak.auctions.repository;

import com.mazadak.auctions.model.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

    Optional<Bid> findByIdempotencyKey(String idempotencyKey);
}

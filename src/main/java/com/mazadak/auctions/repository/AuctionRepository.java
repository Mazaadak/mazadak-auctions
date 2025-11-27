package com.mazadak.auctions.repository;


import com.mazadak.auctions.model.entity.Auction;
import com.mazadak.auctions.model.enumeration.AuctionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.UUID;

public interface AuctionRepository extends JpaRepository<Auction, UUID>, JpaSpecificationExecutor<Auction> {
  
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Auction a WHERE a.id = :id")
    Optional<Auction> findByIdForUpdate(UUID id);
  
    @Query("""
            SELECT a FROM Auction a
            WHERE (
                (a.status IN ('SCHEDULED', 'STARTED', 'ACTIVE', 'PAUSED') AND a.endTime <= :now)
                OR (a.status = 'SCHEDULED' AND a.startTime <= :now)
            )
            """)
    List<Auction> findDueAuctions(LocalDateTime now);


    Optional<Auction> findByProductId(UUID productId);

    Boolean existsByProductId(UUID productId);

    @Query("""
           SELECT EXISTS(
               SELECT 1 FROM Auction a
               WHERE a.productId = :productId
               AND a.deleted = FALSE
               AND a.status IN ('SCHEDULED', 'STARTED', 'ACTIVE', 'PAUSED')
           )
           """)
    Boolean listedAuctionExistsForProduct(UUID productId);

    Optional<Auction> findAuctionByProductIdAndDeletedFalseAndStatusIn(UUID productId, Collection<AuctionStatus> statuses);

    Boolean existsByProductIdAndDeletedFalse(UUID productId);

    Optional<Auction> findByIdempotencyKey(UUID idempotencyKey);
}

package com.mazadak.auctions.repository;

import com.mazadak.auctions.model.entity.ProxyBid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProxyBidRepository extends JpaRepository<ProxyBid, UUID> {
    Optional<ProxyBid> findByAuctionIdAndBidderId(UUID auctionId, UUID BidderId);
    List<ProxyBid> findAllByAuctionId(UUID auctionId);

    @Query("""
        SELECT p
        FROM ProxyBid p
        WHERE p.auctionId = :auctionId
            AND p.maxAmount > :currentHighestAmount
        ORDER BY p.maxAmount DESC, p.createdAt ASC
    """)
    List<ProxyBid> findTopEligibleProxyBids(
            @Param("auctionId") UUID auctionId,
            @Param("currentHighestAmount") BigDecimal currentHighestAmount,
            Pageable pageable
    );

}

package com.mazadak.auctions.repository.specification;

import com.mazadak.auctions.dto.request.AuctionFilterDto;
import com.mazadak.auctions.model.entity.Auction;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuctionSpecifications {
    public static Specification<Auction> hasHighestBidBetween(BigDecimal min, BigDecimal max) {
        return isBetween(min, max, "highestBidPlaced");
    }

    public static Specification<Auction> startsBetween(LocalDateTime start, LocalDateTime end) {
        return isBetween(start, end, "startTime");
    }

    public static Specification<Auction> endsBetween(LocalDateTime start, LocalDateTime end) {
        return isBetween(start, end, "endTime");
    }

    private static <T extends Comparable<T>> Specification<Auction> isBetween(T min, T max, String field) {
        return (root, query, builder) -> {
            if (min == null && max == null) return null;
            if (min == null) return builder.lessThanOrEqualTo(root.get(field), max);
            if (max == null) return builder.greaterThanOrEqualTo(root.get(field), min);
            return builder.between(root.get(field), min, max);
        };
    }

    public static Specification<Auction> hasSellerId(Long sellerId) {
        return (root, query, builder) -> {
            if (sellerId == null) return null;
            return builder.equal(root.get("sellerId"), sellerId);
        };

    }

    public static Specification<Auction> containsTitle(String title) {
        return (root, query, builder) -> {
            if (title == null) return null;
            return builder.like(builder.lower(root.get("title")), "%" + title + "%");
        };
    }

    public static Specification<Auction> buildFromFilter(AuctionFilterDto filter) {
        return Specification.allOf(
                startsBetween(filter.startAfter(), filter.startsBefore()),
                endsBetween(filter.endsAfter(), filter.endsBefore()),
                hasHighestBidBetween(filter.minHighestBid(), filter.maxHighestBid()),
                hasSellerId(filter.sellerId()),
                containsTitle(filter.title())
        );
    }

    // do we need status ?
}

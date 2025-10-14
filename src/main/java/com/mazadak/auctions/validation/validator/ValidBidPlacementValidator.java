package com.mazadak.auctions.validation.validator;


import com.mazadak.auctions.dto.request.PlaceBidRequest;
import com.mazadak.auctions.model.entity.Auction;
import com.mazadak.auctions.model.enumeration.AuctionStatus;
import com.mazadak.auctions.repository.AuctionRepository;
import com.mazadak.auctions.repository.BidRepository;
import com.mazadak.auctions.validation.annotation.ValidBidPlacement;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ValidBidPlacementValidator implements ConstraintValidator<ValidBidPlacement, PlaceBidRequest> {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;

    /**
     * Validates a {@link PlaceBidRequest} for placing a bid on an auction.
     *
     * <p>Checks performed:
     * <ul>
     *   <li>If {@code request} or {@code request.getAuctionId()} is {@code null} the method returns {@code true}
     *       so that null handling can be delegated to other constraints (e.g. {@code @NotNull}).</li>
     *   <li>Verifies the auction exists.</li>
     *   <li>Ensures the auction status is {@link AuctionStatus#STARTED} or {@link AuctionStatus#ACTIVE}.</li>
     *   <li>Ensures the bid amount is at least the current highest bid plus the auction's bid increment.</li>
     *   <li>Prevents the seller from bidding on their own auction.</li>
     *   <li>Ensures the current time is within the auction's start and end times.</li>
     * </ul>
     *
     * This method runs within a read-only transaction since it performs repository reads.
     *
     * @param request the bid placement request to validate
     * @param constraintValidatorContext context used to report constraint violations
     * @return {@code true} if the request is valid; {@code false} and a constraint violation is added otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isValid(PlaceBidRequest request, ConstraintValidatorContext constraintValidatorContext) {
        if (request == null) return true;

        Long auctionId = request.getAuctionId();
        if (auctionId == null) return true;

        Auction auction = auctionRepository.findById(auctionId).orElse(null);
        if (auction == null) {
            return violation(constraintValidatorContext, "Auction does not exist");
        }

        AuctionStatus status = auction.getStatus();
        if (!(status.equals(AuctionStatus.STARTED) || status.equals(AuctionStatus.ACTIVE))) {
            return violation(constraintValidatorContext, "Cannot place bid: auction has not started");
        }

        BigDecimal currentHighestBid = Optional.ofNullable(auction.getHighestBidPlaced()).orElse(auction.getStartingPrice());
        BigDecimal minAllowedBid = currentHighestBid.add(auction.getBidIncrement());
        if (request.getAmount().compareTo(minAllowedBid) < 0) {
            return violation(constraintValidatorContext, "Bid must be at least: " + minAllowedBid);
        }

        if (Objects.equals(request.getBidderId(), auction.getSellerId())) {
            return violation(constraintValidatorContext, "Seller cannot bid on their own auction");
        }

        Instant now = Instant.now();
        Instant startTime = auction.getStartTime().toInstant(ZoneOffset.UTC);
        Instant endTime = auction.getEndTime().toInstant(ZoneOffset.UTC);
        if (now.isBefore(startTime) || now.isAfter(endTime)) {
            return violation(constraintValidatorContext, "Auction is not accepting bids at this time");
        }

        return true;
    }

    private boolean violation(ConstraintValidatorContext constraintValidatorContext, String message) {
        constraintValidatorContext.disableDefaultConstraintViolation();
        constraintValidatorContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }
}

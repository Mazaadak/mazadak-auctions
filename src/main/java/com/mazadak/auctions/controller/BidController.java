package com.mazadak.auctions.controller;

import com.mazadak.auctions.dto.request.PlaceBidRequest;
import com.mazadak.auctions.dto.response.BidResponse;
import com.mazadak.auctions.service.BidService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;

import java.math.BigDecimal;


@Tag(name = "Bids", description = "Operations related to bids")
@RestController
@RequestMapping(
        value = "/auctions",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@AllArgsConstructor
@Validated
public class BidController {

    private final BidService bidService;

    // TODO: authentication
    @Operation(
            summary = "Place a bid on an auction",
            description = "Places a bid for the auction identified by auctionId."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Bid created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BidResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Auction not found", content = @Content)
    })
    @PostMapping("/{auctionId}/bids")
    public ResponseEntity<BidResponse> placeBid(
            @PathVariable Long auctionId,
            @Valid @NotNull @RequestBody PlaceBidRequest request
    ) {
        BidResponse bidResponse = bidService.placeBid(request, auctionId);

        // TODO: Handle XSS Vulnerability
        return ResponseEntity.status(HttpStatus.CREATED).body(bidResponse);
    }


    @Operation(
            summary = "Get bids for an auction",
            description = "Returns a paginated list of bids for the specified auction. Optionally filter by bidderId."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Bids retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BidResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Auction not found", content = @Content)
    })
    @GetMapping("/{auctionId}/bids")
    public ResponseEntity<Page<BidResponse>> getBids(@PathVariable Long auctionId,
                                                     @RequestParam(required = false) Long bidderId,
                                                     @PageableDefault @SortDefault.SortDefaults({
                                                             @SortDefault(sort = "amount", direction = Sort.Direction.DESC),
                                                             @SortDefault(sort = "createdAt", direction = Sort.Direction.ASC)
                                                     }) Pageable pageable) {
        return ResponseEntity.ok(bidService.getBids(auctionId, bidderId, pageable));
    }


    @Operation(
            summary = "Get highest bid for an auction",
            description = "Returns the highest bid amount for the specified auction."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Highest bid retrieved",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BigDecimal.class))
            ),
            @ApiResponse(responseCode = "404", description = "Auction not found", content = @Content)
    })
    @GetMapping("/{auctionId}/bids/highest")
    public ResponseEntity<BigDecimal> getHighestBid(@PathVariable Long auctionId) {
        BigDecimal highestBid = bidService.getHighestBid(auctionId);
        return ResponseEntity.ok(highestBid);
    }

    @Operation(
            summary = "Get bids by bidder",
            description = "Returns a paginated list of bids placed by the specified bidder across auctions."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Bids retrieved for bidder",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = BidResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Bidder not found", content = @Content)
    })
    @GetMapping("/bidder/{bidderId}/bids")
    public ResponseEntity<Page<BidResponse>> getBidsByBidder(@PathVariable Long bidderId,
                                                             @PageableDefault(sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(bidService.getBidsByBidder(bidderId, pageable));
    }

}
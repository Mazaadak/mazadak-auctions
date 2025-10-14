package com.mazadak.auctions.controller;

import com.mazadak.auctions.dto.request.PlaceBidRequest;
import com.mazadak.auctions.dto.response.BidResponse;
import com.mazadak.auctions.service.BidService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    @PostMapping("/{id}/bids")
    public ResponseEntity<BidResponse> placeBid(
            @Parameter(description = "ID of the auction", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Bid details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PlaceBidRequest.class))
            )
            @Valid @RequestBody PlaceBidRequest request
    ) {
        // TODO: handle if the auctionId in /{id}/bids is different than the auctionId inside the request body
        BidResponse bidResponse = bidService.placeBid(request);

        // TODO: Handle XSS Vulnerability
        return ResponseEntity.status(HttpStatus.CREATED).body(bidResponse);
    }

    // TODO: consider limiting the sort fields to control the filtering more
    @GetMapping("/{id}/bids")
    public ResponseEntity<Page<BidResponse>> getBids(@PathVariable Long id,
                                                     @RequestParam(required = false) Long bidderId,
                                                     @PageableDefault(size = 10) @SortDefault.SortDefaults({
                                                             @SortDefault(sort = "amount", direction = Sort.Direction.DESC),
                                                             @SortDefault(sort = "createdAt", direction = Sort.Direction.ASC)
                                                     }) Pageable pageable) {
        return ResponseEntity.ok(bidService.getBids(id, bidderId, pageable));
    }

    @GetMapping("/{id}/bids/highest")
    public ResponseEntity<BigDecimal> getHighestBid(@PathVariable Long id) {
        BigDecimal highestBid = bidService.getHighestBid(id);
        return ResponseEntity.ok(highestBid);
    }

}
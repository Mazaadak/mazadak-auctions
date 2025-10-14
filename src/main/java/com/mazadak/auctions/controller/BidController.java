package com.mazadak.auctions.controller;

import com.mazadak.auctions.dto.request.PlaceBidRequest;
import com.mazadak.auctions.dto.response.BidResponse;
import com.mazadak.auctions.service.BidService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
        BidResponse bidResponse = bidService.placeBid(request);

        // TODO: Handle XSS Vulnerability
        return ResponseEntity.status(HttpStatus.CREATED).body(bidResponse);
    }
}
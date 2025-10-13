package com.mazadak.auctions.controller;

import com.mazadak.auctions.dto.request.PlaceBidRequest;
import com.mazadak.auctions.dto.response.BidResponse;
import com.mazadak.auctions.service.BidService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/auctions",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@AllArgsConstructor
public class BidController {

    private final BidService bidService;

    @PostMapping("/{auctionId}/bids")
    public ResponseEntity<BidResponse> placeBid(
            @PathVariable Long auctionId,
            @RequestBody PlaceBidRequest request
            ) {
        BidResponse bidResponse = bidService.placeBid(request);

        // TODO: Handle XSS Vulnerability
        return ResponseEntity.status(HttpStatus.CREATED).body(bidResponse);
    }
}

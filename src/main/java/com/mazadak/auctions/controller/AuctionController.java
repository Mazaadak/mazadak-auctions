package com.mazadak.auctions.controller;

import com.mazadak.auctions.dto.request.AuctionFilterDto;
import com.mazadak.auctions.dto.request.CreateAuctionRequest;
import com.mazadak.auctions.dto.request.UpdateAuctionRequest;
import com.mazadak.auctions.dto.response.AuctionResponse;
import com.mazadak.auctions.service.AuctionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/auctions",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@Validated
@AllArgsConstructor
public class AuctionController {
    private final AuctionService auctionService;
    private final Logger logger = LoggerFactory.getLogger(AuctionController.class);

    @GetMapping("{id}")
    ResponseEntity<AuctionResponse> findAuctionById(@PathVariable Long id) {
        return ResponseEntity.ok(auctionService.findAuctionById(id));
    }

    @GetMapping
    Page<AuctionResponse> findAuctionsByCriteria(
            @ModelAttribute AuctionFilterDto filter,
            @PageableDefault(sort = "startTime", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return auctionService.findAuctionsByCriteria(filter, pageable);
    }

    // TODO: authorization
    @PutMapping("{id}")
    ResponseEntity<AuctionResponse> updateAuction(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateAuctionRequest request) {
        return ResponseEntity.ok(auctionService.updateAuction(id, request));
    }

    // TODO: authorization
    @DeleteMapping("{id}")
    ResponseEntity<Void> deleteAuction(@PathVariable Long id) {
        auctionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // TODO: authentication
    @PostMapping
    ResponseEntity<AuctionResponse> createAuction(@Valid @RequestBody CreateAuctionRequest dto) {
        return ResponseEntity.ok(auctionService.createAuction(dto));
    }

    // TODO: authorization
    @PostMapping("{id}/cancel")
    ResponseEntity<AuctionResponse> cancelAuction(@PathVariable Long id) {
        return ResponseEntity.ok(auctionService.cancelAuction(id));
    }

    // TODO: authorization
    @PostMapping("{id}/pause")
    ResponseEntity<AuctionResponse> pauseAuction(@PathVariable Long id) {
        return ResponseEntity.ok(auctionService.pauseAuction(id));
    }

    // TODO: authorization
    @PostMapping("{id}/resume")
    ResponseEntity<AuctionResponse> resumeAuction(@PathVariable Long id) {
        return ResponseEntity.ok(auctionService.resumeAuction(id));
    }
}

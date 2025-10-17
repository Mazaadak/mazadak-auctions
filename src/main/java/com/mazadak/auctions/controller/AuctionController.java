package com.mazadak.auctions.controller;

import com.mazadak.auctions.dto.request.AuctionFilterDto;
import com.mazadak.auctions.dto.request.CreateAuctionRequest;
import com.mazadak.auctions.dto.request.UpdateAuctionRequest;
import com.mazadak.auctions.dto.response.AuctionResponse;
import com.mazadak.auctions.service.AuctionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(
        value = "/auctions",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@Validated
@AllArgsConstructor
@CrossOrigin("*") // TODO: remove
public class AuctionController {
    private final AuctionService auctionService;
    private final Logger logger = LoggerFactory.getLogger(AuctionController.class);

    @GetMapping("{id}")
    ResponseEntity<AuctionResponse> findAuctionById(@PathVariable UUID id) {
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
    ResponseEntity<AuctionResponse> updateAuction(@PathVariable UUID id,
                                                  @Valid @RequestBody UpdateAuctionRequest request) {
        return ResponseEntity.ok(auctionService.updateAuction(id, request));
    }

    // TODO: authorization
    @DeleteMapping("{id}")
    ResponseEntity<Void> deleteAuction(@PathVariable UUID id) {
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
    ResponseEntity<AuctionResponse> cancelAuction(@PathVariable UUID id) {
        return ResponseEntity.ok(auctionService.cancelAuction(id));
    }

    // TODO: authorization
    @PostMapping("{id}/pause")
    ResponseEntity<AuctionResponse> pauseAuction(@PathVariable UUID id) {
        return ResponseEntity.ok(auctionService.pauseAuction(id));
    }

    // TODO: authorization
    @PostMapping("{id}/resume")
    ResponseEntity<AuctionResponse> resumeAuction(@PathVariable UUID id) {
        return ResponseEntity.ok(auctionService.resumeAuction(id));
    }

    @PostMapping("{id}/watch")
    ResponseEntity<Void> watchAuction(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        auctionService.addWatcher(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("{id}/unwatch")
    ResponseEntity<Void> unwatchAuction(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        auctionService.removeWatcher(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{id}/watchers")
    ResponseEntity<List<Long>> getWatchers(@PathVariable UUID id) {
        return ResponseEntity.ok(auctionService.getWatcherIds(id));
    }
}

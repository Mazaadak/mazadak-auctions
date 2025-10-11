package com.mazadak.auctions.controller;

import com.mazadak.auctions.dto.AuctionDto;
import com.mazadak.auctions.dto.AuctionFilterDto;
import com.mazadak.auctions.dto.request.CreateAuctionRequest;
import com.mazadak.auctions.model.entity.Auction;
import com.mazadak.auctions.service.AuctionService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    ResponseEntity<AuctionDto> findAuctionById(@PathVariable Long id) {
        return ResponseEntity.ok(auctionService.findAuctionById(id));
    }

    @GetMapping
    Page<AuctionDto> findAuctionsByCriteria(
            @ModelAttribute AuctionFilterDto filter,
            @PageableDefault(sort = "startTime", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return auctionService.findAuctionsByCriteria(filter, pageable);
    }

    // temp
    @PostMapping
    ResponseEntity<Auction> createAuction(@RequestBody CreateAuctionRequest dto) {
        return ResponseEntity.ok(auctionService.createAuction(dto));
    }
}

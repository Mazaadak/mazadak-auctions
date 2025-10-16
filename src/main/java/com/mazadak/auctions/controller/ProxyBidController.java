package com.mazadak.auctions.controller;

import com.mazadak.auctions.dto.request.ProxyBidRequest;
import com.mazadak.auctions.dto.response.BidResponse;
import com.mazadak.auctions.dto.response.ProxyBidResponse;
import com.mazadak.auctions.service.ProxyBidService;
import com.mazadak.auctions.service.support.UpsertResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.apache.coyote.Request;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Proxy bids", description = "Operations to related to proxy bids")
@RestController
@RequestMapping(
        value = "auctions/{auctionId}/proxy-bids/",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@AllArgsConstructor
@Validated
public class ProxyBidController {

    private final ProxyBidService proxyBidService;

    // TODO: authentication
    @Operation(
            summary = "Create or update a proxy bid on an auction",
            description = "Create or update a proxy bid for the auction identified by auctionId."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Proxy bid created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProxyBidResponse.class))
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "Proxy bid updated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProxyBidResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Auction not found", content = @Content)
    })
    @PutMapping("/{bidderId}")
    public ResponseEntity<ProxyBidResponse> upsertProxyBid(
            @PathVariable Long auctionId,
            @PathVariable Long bidderId,
            @Valid @NotNull @RequestBody ProxyBidRequest request
    ) {

        UpsertResult result = proxyBidService.upsertProxyBid(request, auctionId, bidderId);
        HttpStatus status = (result.created() ? HttpStatus.CREATED : HttpStatus.OK);
        URI location = URI.create(String.format("auctions/%d/proxy-bids/%d", auctionId, bidderId));

        return ResponseEntity.status(status)
                .location(location)
                .body(result.response());
    }

    @GetMapping("/{bidderId}")
    public ResponseEntity<ProxyBidResponse> getProxyBid(@PathVariable Long auctionId,
                                                        @PathVariable Long bidderId) {
        return ResponseEntity.ok(proxyBidService.getProxyBid(auctionId, bidderId));
    }

    @DeleteMapping("/{bidderId}")
    public ResponseEntity<Void> deleteProxyBid(@PathVariable Long auctionId,
                                               @PathVariable Long bidderId) {
        proxyBidService.deleteProxyBid(auctionId, bidderId);
        return ResponseEntity.noContent().build();
    }

}

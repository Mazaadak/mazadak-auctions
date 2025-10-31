package com.mazadak.auctions.dto.event;

import com.mazadak.auctions.dto.response.AuctionResponse;
import com.mazadak.auctions.dto.response.AuctionWatchResponse;
import com.mazadak.auctions.dto.response.BidderInfo;

import java.util.List;

public record AuctionEndedEvent(AuctionResponse auction,
                                List<BidderInfo> bidders,
                                List<AuctionWatchResponse> watchlist) {
}

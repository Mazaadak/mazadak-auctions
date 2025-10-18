package com.mazadak.auctions.util;

import com.mazadak.auctions.dto.response.ProxyBidResponse;

public record ProxyBidUpsertResult(ProxyBidResponse response, boolean created) {
}

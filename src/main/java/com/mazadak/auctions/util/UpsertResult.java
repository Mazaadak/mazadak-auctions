package com.mazadak.auctions.util;

import com.mazadak.auctions.dto.response.ProxyBidResponse;

public record UpsertResult(ProxyBidResponse response, boolean created) {
}

package com.mazadak.auctions.service.support;

import com.mazadak.auctions.dto.response.ProxyBidResponse;

public record UpsertResult(ProxyBidResponse response, boolean created) {
}

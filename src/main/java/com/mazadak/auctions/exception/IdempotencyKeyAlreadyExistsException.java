package com.mazadak.auctions.exception;

import com.mazadak.auctions.dto.request.CreateAuctionRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IdempotencyKeyAlreadyExistsException extends RuntimeException {
    public IdempotencyKeyAlreadyExistsException(UUID idempotencyKey, String resource, Object arg) {
        super(String.format("Attempted to reuse idempotency key %s for resource %s with arg %s", idempotencyKey.toString(), resource, arg.toString()));
    }
}

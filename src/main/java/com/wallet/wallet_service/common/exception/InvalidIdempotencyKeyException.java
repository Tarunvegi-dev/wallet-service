package com.wallet.wallet_service.common.exception;

public class InvalidIdempotencyKeyException extends RuntimeException {
    public InvalidIdempotencyKeyException(String message){
        super(message);
    }
}

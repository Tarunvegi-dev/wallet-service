package com.wallet.wallet_service.common.exception;

public class SamePasswordException extends RuntimeException
{
    public SamePasswordException(String message) {
        super(message);
    }
}

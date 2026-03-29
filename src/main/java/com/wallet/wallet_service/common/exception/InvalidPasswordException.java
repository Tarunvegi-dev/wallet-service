package com.wallet.wallet_service.common.exception;

public class InvalidPasswordException extends RuntimeException
{
    public InvalidPasswordException(String message) {
        super(message);
    }
}

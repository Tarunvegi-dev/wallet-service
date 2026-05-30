package com.wallet.wallet_service.common.exception;

public class TransactionNotFoundException extends RuntimeException{
    public TransactionNotFoundException(String message){
        super(message);
    }
}

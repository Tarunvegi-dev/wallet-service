package com.wallet.wallet_service.common.exception;

public class WalletAlreadyExistsException extends RuntimeException{
    public WalletAlreadyExistsException(String message){
        super(message);
    }
}

package com.wallet.wallet_service.common.exception;

public class WalletNotFoundException extends RuntimeException{
    public WalletNotFoundException(String message){
        super(message);
    }
}

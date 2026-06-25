package com.wallet.wallet_service.transaction.service;

import com.wallet.wallet_service.transaction.dto.CreateTransactionRequest;
import com.wallet.wallet_service.transaction.dto.CreateTransactionResponse;
import com.wallet.wallet_service.transaction.dto.GetTransactionResponse;

public interface TransactionService {
    public CreateTransactionResponse createTransaction(Long userId, CreateTransactionRequest createTransactionRequest);
    
    public GetTransactionResponse getTransaction(Long transctionId);
}

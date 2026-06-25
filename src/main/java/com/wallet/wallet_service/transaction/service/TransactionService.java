package com.wallet.wallet_service.transaction.service;

import com.wallet.wallet_service.transaction.dto.CreateTransactionRequest;
import com.wallet.wallet_service.transaction.dto.CreateTransactionResponse;

public interface TransactionService {
    public CreateTransactionResponse createTransaction(Long userId, CreateTransactionRequest createTransactionRequest);
}

package com.wallet.wallet_service.transaction.service;

import java.math.BigDecimal;

import com.wallet.wallet_service.payment.client.dto.PaymentResponse;
import com.wallet.wallet_service.transaction.dto.CreateTransactionRequest;
import com.wallet.wallet_service.transaction.enums.PaymentMode;
import com.wallet.wallet_service.transaction.enums.TransactionType;
import com.wallet.wallet_service.transaction.model.Transaction;
import com.wallet.wallet_service.wallet.model.Wallet;

public interface TransactionPersistenceService {
     public Transaction createPendingTransaction(BigDecimal amount, PaymentMode paymentMode, Long walletId, Long referenceTransactionId, TransactionType transactionType);

     public Transaction updateWalletBalanceAndMarkTransactionStatus(CreateTransactionRequest createTransactionRequest, Wallet wallet, TransactionType transactionType, Transaction transaction, PaymentResponse paymentResponse); 
}

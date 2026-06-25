package com.wallet.wallet_service.transaction.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.wallet.wallet_service.common.exception.TransactionNotFoundException;
import com.wallet.wallet_service.payment.client.dto.PaymentResponse;
import com.wallet.wallet_service.transaction.dto.CreateTransactionRequest;
import com.wallet.wallet_service.transaction.enums.PaymentMode;
import com.wallet.wallet_service.transaction.enums.TransactionStatus;
import com.wallet.wallet_service.transaction.enums.TransactionType;
import com.wallet.wallet_service.transaction.model.Transaction;
import com.wallet.wallet_service.transaction.repository.TransactionRepository;
import com.wallet.wallet_service.transaction.service.TransactionPersistenceService;
import com.wallet.wallet_service.wallet.model.Wallet;
import com.wallet.wallet_service.wallet.service.WalletService;

import jakarta.transaction.Transactional;

@Service
public class TransactionPersistenceServiceImpl implements TransactionPersistenceService{
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;

    public TransactionPersistenceServiceImpl(TransactionRepository transactionRepository, WalletService walletService){
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
    }

    @Transactional
    @Override
    public Transaction createPendingTransaction(BigDecimal amount, PaymentMode paymentMode, Long walletId, Long referenceTransactionId, TransactionType transactionType) {
            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setPaymentMode(paymentMode);
            if(transactionType == TransactionType.DEBIT){
                transaction.setSenderWalletId(walletId);
            }else if(transactionType == TransactionType.CREDIT){
                transaction.setReceiverWalletId(walletId);
            }else{
                Transaction originalTransaction = transactionRepository.findById(referenceTransactionId)
                                                .orElseThrow(() -> new TransactionNotFoundException("Invalid reference transaction id"));
                if(originalTransaction.getTransactionType() == TransactionType.CREDIT){
                    transaction.setSenderWalletId(walletId);
                }else{
                    transaction.setReceiverWalletId(walletId);
                }
                transaction.setReferenceTransactionId(referenceTransactionId);
            }
            transaction.setTransactionStatus(TransactionStatus.PENDING);
            transaction.setTransactionType(transactionType);
            transactionRepository.save(transaction);
            return transaction;
    }

    @Override
    @Transactional
    public Transaction updateWalletBalanceAndMarkTransactionStatus(CreateTransactionRequest createTransactionRequest, Wallet wallet,
            TransactionType transactionType, Transaction transaction, PaymentResponse paymentResponse) {
        if(paymentResponse.getPaymentStatus() == TransactionStatus.SUCCESS){
            if(transactionType == TransactionType.DEBIT){
                walletService.debit(wallet.getWalletId(), createTransactionRequest.getAmount());
            }else if(transactionType == TransactionType.CREDIT){
                walletService.credit(wallet.getWalletId(), createTransactionRequest.getAmount());
            }else{
                Transaction originalTransaction = transactionRepository.findById(createTransactionRequest.getReferenceTransactionId())
                                    .orElseThrow(() -> new TransactionNotFoundException("Invalid reference transaction id"));
    
                if(originalTransaction.getTransactionType() == TransactionType.CREDIT){
                    walletService.debit(wallet.getWalletId(), originalTransaction.getAmount());
                }else{
                    walletService.credit(wallet.getWalletId(), originalTransaction.getAmount());
                }
                originalTransaction.setTransactionStatus(TransactionStatus.REFUNDED);
            }     
            transaction.setTransactionStatus(TransactionStatus.SUCCESS);   
        }else{
            transaction.setTransactionStatus(TransactionStatus.FAILED);
        }
        transactionRepository.save(transaction);
        return transaction;
    }
    
}

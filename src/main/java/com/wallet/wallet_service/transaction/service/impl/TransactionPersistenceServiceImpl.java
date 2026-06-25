package com.wallet.wallet_service.transaction.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.wallet_service.common.exception.TransactionNotFoundException;
import com.wallet.wallet_service.payment.client.dto.CompensatePaymentRequest;
import com.wallet.wallet_service.payment.client.dto.PaymentResponse;
import com.wallet.wallet_service.transaction.dto.CreateTransactionRequest;
import com.wallet.wallet_service.transaction.enums.OutboxStatus;
import com.wallet.wallet_service.transaction.enums.PaymentMode;
import com.wallet.wallet_service.transaction.enums.TransactionStatus;
import com.wallet.wallet_service.transaction.enums.TransactionType;
import com.wallet.wallet_service.transaction.model.OutboxEvent;
import com.wallet.wallet_service.transaction.model.Transaction;
import com.wallet.wallet_service.transaction.repository.OutboxRepository;
import com.wallet.wallet_service.transaction.repository.TransactionRepository;
import com.wallet.wallet_service.transaction.service.TransactionPersistenceService;
import com.wallet.wallet_service.wallet.model.Wallet;
import com.wallet.wallet_service.wallet.service.WalletService;

import jakarta.transaction.Transactional;

@Service
public class TransactionPersistenceServiceImpl implements TransactionPersistenceService{
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;


    public TransactionPersistenceServiceImpl(TransactionRepository transactionRepository, WalletService walletService, OutboxRepository outboxRepository, ObjectMapper objectMapper){
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
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
        
        if(paymentResponse.getPaymentStatus() == TransactionStatus.FAILED){
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            if(transactionType == TransactionType.DEBIT){
                walletService.releaseReserveBalance(wallet.getWalletId(), createTransactionRequest.getAmount());
            }else if(transactionType == TransactionType.REFUND){
                 Transaction originalTransaction = transactionRepository.findById(createTransactionRequest.getReferenceTransactionId())
                                    .orElseThrow(() -> new TransactionNotFoundException("Invalid reference transaction id"));
    
                if(originalTransaction.getTransactionType() == TransactionType.CREDIT){
                    walletService.releaseReserveBalance(wallet.getWalletId(), originalTransaction.getAmount());
                }
            }
            transactionRepository.save(transaction);
        } else{
            try {                
                if(transactionType == TransactionType.DEBIT){
                    walletService.debit(wallet.getWalletId(), createTransactionRequest.getAmount(), false);
                }else if(transactionType == TransactionType.CREDIT){
                    walletService.credit(wallet.getWalletId(), createTransactionRequest.getAmount());
                }else{
                    Transaction originalTransaction = transactionRepository.findById(createTransactionRequest.getReferenceTransactionId())
                                        .orElseThrow(() -> new TransactionNotFoundException("Invalid reference transaction id"));
        
                    if(originalTransaction.getTransactionType() == TransactionType.CREDIT){
                        walletService.debit(wallet.getWalletId(), originalTransaction.getAmount(), false);
                    }else{
                        walletService.credit(wallet.getWalletId(), originalTransaction.getAmount());
                    }
                    originalTransaction.setTransactionStatus(TransactionStatus.REFUNDED);
                }     
                transaction.setTransactionStatus(TransactionStatus.SUCCESS);   
                transactionRepository.save(transaction);
            } catch (Exception e) {
                transaction.setTransactionStatus(TransactionStatus.COMPENSATION_PENDING);
                transactionRepository.save(transaction);  

                //storing the compensation request event in outbox to prevent message loss incase of broker failure
                CompensatePaymentRequest compensatePaymentRequest = new CompensatePaymentRequest(transaction.getTransactionId(), wallet.getUserId());
                
                try {
                    String payload = objectMapper.writeValueAsString(compensatePaymentRequest);
                    OutboxEvent outboxEvent = new OutboxEvent();
                    outboxEvent.setTransactionId(transaction.getTransactionId());
                    outboxEvent.setStatus(OutboxStatus.PENDING);
                    outboxEvent.setEventType("compensation-requested");
                    outboxEvent.setPayload(payload);
                    outboxRepository.save(outboxEvent);
                }catch(Exception ex){
                    throw new RuntimeException("Failed to serialize the compensation request", ex);
                }
            }
        }

        return transaction;
    
    }

    
}

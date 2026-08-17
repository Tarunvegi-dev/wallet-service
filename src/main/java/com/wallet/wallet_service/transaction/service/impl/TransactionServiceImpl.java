package com.wallet.wallet_service.transaction.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.wallet.wallet_service.common.exception.InvalidIdempotencyKeyException;
import com.wallet.wallet_service.common.exception.InvalidTransactionException;
import com.wallet.wallet_service.common.exception.TransactionNotFoundException;
import com.wallet.wallet_service.common.exception.WalletNotFoundException;
import com.wallet.wallet_service.payment.client.PaymentClient;
import com.wallet.wallet_service.payment.client.dto.CompensatePaymentRequest;
import com.wallet.wallet_service.payment.client.dto.PaymentRequest;
import com.wallet.wallet_service.payment.client.dto.PaymentResponse;
import com.wallet.wallet_service.transaction.dto.CreateTransactionRequest;
import com.wallet.wallet_service.transaction.dto.CreateTransactionResponse;
import com.wallet.wallet_service.transaction.dto.GetTransactionResponse;
import com.wallet.wallet_service.transaction.enums.TransactionStatus;
import com.wallet.wallet_service.transaction.enums.TransactionType;
import com.wallet.wallet_service.transaction.model.Transaction;
import com.wallet.wallet_service.transaction.repository.TransactionRepository;
import com.wallet.wallet_service.transaction.service.TransactionService;
import com.wallet.wallet_service.wallet.model.Wallet;
import com.wallet.wallet_service.wallet.service.WalletService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService{
    
    private final WalletService walletService;
    private final TransactionRepository transactionRepository;
    private final PaymentClient paymentClient;
    private final TransactionPersistenceServiceImpl transactionPersistenceServiceImpl;
    private static final String COMPENSATION_QUEUE = "compensation-completed";

    public TransactionServiceImpl(WalletService walletService, TransactionRepository transactionRepository, PaymentClient paymentClient, TransactionPersistenceServiceImpl transactionPersistenceServiceImpl){
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
        this.paymentClient = paymentClient;
        this.transactionPersistenceServiceImpl = transactionPersistenceServiceImpl;
    }

    @RabbitListener(queues = COMPENSATION_QUEUE)
    @Transactional
    public void compensationCompleted(CompensatePaymentRequest compensatePaymentRequest){
        Transaction transaction = transactionRepository.findById(compensatePaymentRequest.getTransactionId())
                                        .orElseThrow(() -> new TransactionNotFoundException("Invalid reference transaction id")); 
        //to prevent duplicate compensation
        if(transaction.getTransactionStatus() == TransactionStatus.FAILED) {
            log.info("Duplicate compensation ignored ={}", transaction.getTransactionId());
            return;
        }
        TransactionType transactionType = transaction.getTransactionType();
        transaction.setTransactionStatus(TransactionStatus.FAILED);
        Wallet wallet = walletService.getWalletByUserId(compensatePaymentRequest.getUserId()).orElseThrow(() -> new WalletNotFoundException("Wallet doesn't exist for you, please create a wallet before doing the transaction"));
        if(transactionType == TransactionType.REFUND){
            Transaction originalTransaction = transactionRepository.findById(transaction.getReferenceTransactionId())
                                .orElseThrow(() -> new TransactionNotFoundException("Invalid reference transaction id"));

            if(originalTransaction.getTransactionType() == TransactionType.CREDIT){
                walletService.credit(wallet.getWalletId(), originalTransaction.getAmount());
            }else{
                walletService.debit(wallet.getWalletId(), originalTransaction.getAmount(), true);
            }
        }else{
            if(transactionType == TransactionType.DEBIT){
                walletService.credit(wallet.getWalletId(), transaction.getAmount());
            }else{
                walletService.debit(wallet.getWalletId(), transaction.getAmount(), true);
            }   
        }
        log.info("compensation completed ={}", transaction.getTransactionId());
        transactionRepository.save(transaction);
    }

    private boolean isRetryable(TransactionStatus transactionStatus){
        return transactionStatus == TransactionStatus.PENDING || transactionStatus == TransactionStatus.PAYMENT_UNKNOWN;
    }

    private boolean isConflict(String requestHash, String transactionHash){
        return !requestHash.equals(transactionHash);
    }

    private String getHash(Long walletId, CreateTransactionRequest request){
        String payload = walletId + "|" + request.getAmount() + "|" + request.getTransactionType() + "|" + 
        request.getReferenceTransactionId() + "|" + request.getPaymentMode();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            // TODO: handle exception
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }


    @Override
    public CreateTransactionResponse createTransaction(Long userId, CreateTransactionRequest createTransactionRequest, String idempotencyKey) {
        Wallet wallet = walletService.getWalletByUserId(userId).orElseThrow(() -> new WalletNotFoundException("Wallet doesn't exist for you, please create a wallet before doing the transaction"));
        
        Optional<Transaction> existingTransaction = transactionRepository.findByIdempotencyKey(idempotencyKey);
        TransactionType transactionType = createTransactionRequest.getTransactionType();
        CreateTransactionResponse createTransactionResponse = new CreateTransactionResponse();

        String requestHash = getHash(wallet.getWalletId(), createTransactionRequest);
        if(existingTransaction.isPresent()){
            Transaction transaction = existingTransaction.get();
            String transactionHash = transaction.getRequestHash();
            if(isConflict(requestHash, transactionHash)){
                throw new InvalidIdempotencyKeyException("Idempotency-Key has already been used with different request");
            }
            createTransactionResponse.setTransactionid(transaction.getTransactionId());
            if(isRetryable(transaction.getTransactionStatus())){
                transaction = processPayment(createTransactionRequest, wallet, transaction);
            }
            createTransactionResponse.setTransactionStatus(transaction.getTransactionStatus());
            return createTransactionResponse;
        }else{
            Transaction transaction;
    
            //validation for refund and debit transactions
            if(transactionType == TransactionType.REFUND){
                validateRefund(wallet, createTransactionRequest.getReferenceTransactionId());
            }else if(transactionType == TransactionType.DEBIT){
                walletService.validateBalance(wallet.getWalletId(), createTransactionRequest.getAmount());
            }
    
            //create pending transaction
            if(transactionType == TransactionType.REFUND){
                Transaction originalTransaction = transactionRepository.findById(createTransactionRequest.getReferenceTransactionId())
                                                .orElseThrow(() -> new TransactionNotFoundException("Invalid reference transaction id"));
                transaction = transactionPersistenceServiceImpl.createPendingTransaction(originalTransaction.getAmount(), originalTransaction.getPaymentMode(), wallet.getWalletId(), createTransactionRequest.getReferenceTransactionId(), transactionType, idempotencyKey, requestHash);
            }else{
                transaction = transactionPersistenceServiceImpl.createPendingTransaction(createTransactionRequest.getAmount(), createTransactionRequest.getPaymentMode(), wallet.getWalletId(), null, transactionType, idempotencyKey, requestHash);
            }
            
            createTransactionResponse.setTransactionid(transaction.getTransactionId());
            transaction = processPayment(createTransactionRequest, wallet, transaction);
            createTransactionResponse.setTransactionStatus(transaction.getTransactionStatus());
            return createTransactionResponse;
        }

     
    }

    private Transaction processPayment(CreateTransactionRequest createTransactionRequest, Wallet wallet, Transaction transaction) {
        try{
            //call external payment service
            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setAmount(transaction.getAmount());
            paymentRequest.setPaymentMode(transaction.getPaymentMode());
            paymentRequest.setTransactionId(transaction.getTransactionId());
            paymentRequest.setTransactionType(transaction.getTransactionType());
            PaymentResponse paymentResponse = new PaymentResponse();
            paymentResponse = paymentClient.processPayment(paymentRequest);
   
            //update wallet balance and mark transaction status according to payment response
            transaction = transactionPersistenceServiceImpl.updateWalletBalanceAndMarkTransactionStatus(createTransactionRequest, wallet, transaction, paymentResponse);
        }catch(Exception e){
            log.warn("Payment unknown ={}", transaction.getTransactionId(), e);
            transaction.setTransactionStatus(TransactionStatus.PAYMENT_UNKNOWN);
            transactionRepository.save(transaction);
        }
        return transaction;
    }



    private void validateRefund(Wallet wallet, Long referenceTransactionId){
        Transaction originalTransaction = transactionRepository.findById(referenceTransactionId)
                                            .orElseThrow(() -> new TransactionNotFoundException("Invalid reference transaction id"));
        TransactionType orgTransactionType = originalTransaction.getTransactionType();
        if(orgTransactionType == TransactionType.REFUND){
            throw new InvalidTransactionException("Refund transaction cannot be refunded");
        }
        if(originalTransaction.getTransactionStatus() !=  TransactionStatus.SUCCESS){
            throw new InvalidTransactionException("Pending/Failed/Refunded transactions cannot be refunded");
        }
        if(orgTransactionType == TransactionType.CREDIT && wallet.getWalletId() != originalTransaction.getReceiverWalletId()){
            throw new InvalidTransactionException("Reference transaction doesn't belong to your wallet");
        }
        if(orgTransactionType == TransactionType.DEBIT && wallet.getWalletId() != originalTransaction.getSenderWalletId()){
            throw new InvalidTransactionException("Reference transaction doesn't belong to your wallet");
        }
        if(orgTransactionType == TransactionType.CREDIT){
            walletService.validateBalance(wallet.getWalletId(), originalTransaction.getAmount());
        }
    }

    @Override
    public GetTransactionResponse getTransaction(Long transctionId) {
        GetTransactionResponse response = new GetTransactionResponse();
        Transaction transaction = transactionRepository.findById(transctionId).orElseThrow(() -> new InvalidTransactionException("invalid transaction id"));
        response.setAmount(transaction.getAmount());
        response.setPaymentMode(transaction.getPaymentMode());
        response.setReferenceTransactionId(transaction.getReferenceTransactionId());
        response.setTransactionId(transctionId);
        response.setTransactionType(transaction.getTransactionType());
        response.setTransactionStatus(transaction.getTransactionStatus());
        return response;
    }


    
}

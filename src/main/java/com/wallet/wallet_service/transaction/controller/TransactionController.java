package com.wallet.wallet_service.transaction.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.wallet_service.transaction.dto.CreateTransactionRequest;
import com.wallet.wallet_service.transaction.dto.CreateTransactionResponse;
import com.wallet.wallet_service.transaction.enums.TransactionType;
import com.wallet.wallet_service.transaction.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }
    
    @PostMapping("")
    public ResponseEntity<CreateTransactionResponse> createTransaction(@AuthenticationPrincipal String userId, @RequestBody @Valid CreateTransactionRequest createTransactionRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(Long.valueOf(userId), createTransactionRequest));
    }
}

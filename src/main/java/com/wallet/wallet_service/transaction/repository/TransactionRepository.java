package com.wallet.wallet_service.transaction.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wallet.wallet_service.transaction.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{
    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);   
}

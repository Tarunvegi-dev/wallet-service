package com.wallet.wallet_service.wallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wallet.wallet_service.wallet.model.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    public Optional<Wallet> findByUserId(Long userId);
}

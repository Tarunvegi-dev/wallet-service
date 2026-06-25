package com.wallet.wallet_service.wallet.service;

import java.math.BigDecimal;
import java.util.Optional;

import com.wallet.wallet_service.wallet.dto.CreateWalletResponse;
import com.wallet.wallet_service.wallet.dto.GetWalletBalanceResponse;
import com.wallet.wallet_service.wallet.model.Wallet;

public interface WalletService {
    public CreateWalletResponse createWallet(Long userId);

    public GetWalletBalanceResponse getWalletBalance(Long userId);

    public void credit(Long walletId, BigDecimal amount);

    public void debit(Long walletId, BigDecimal amount, boolean isCompensation);
    
    public void validateBalance(Long walletId, BigDecimal amount);

    public void releaseReserveBalance(Long walletId, BigDecimal amount);

    public Optional<Wallet> getWalletByUserId(Long userId);
}

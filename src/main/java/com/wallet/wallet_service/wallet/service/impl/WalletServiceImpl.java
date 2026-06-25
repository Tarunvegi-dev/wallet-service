package com.wallet.wallet_service.wallet.service.impl;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.wallet.wallet_service.common.exception.InsufficientFundsException;
import com.wallet.wallet_service.common.exception.UserNotFoundException;
import com.wallet.wallet_service.common.exception.WalletAlreadyExistsException;
import com.wallet.wallet_service.common.exception.WalletNotFoundException;
import com.wallet.wallet_service.user.repository.UserRepository;
import com.wallet.wallet_service.wallet.dto.CreateWalletResponse;
import com.wallet.wallet_service.wallet.dto.GetWalletBalanceResponse;
import com.wallet.wallet_service.wallet.enums.WalletStatus;
import com.wallet.wallet_service.wallet.model.Wallet;
import com.wallet.wallet_service.wallet.repository.WalletRepository;
import com.wallet.wallet_service.wallet.service.WalletService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class WalletServiceImpl implements WalletService{

    final WalletRepository walletRepository;
    final UserRepository userRepository;

    @Override
    public CreateWalletResponse createWallet(Long userId) {
        if(!userRepository.existsById(userId)){
            throw new UserNotFoundException("User does not exists with this userId");
        }
        if(walletRepository.findByUserId(userId).isPresent()){
            throw new WalletAlreadyExistsException("Wallet already exists with this userId");
        }
        Wallet wallet = new Wallet();
        wallet.setStatus(WalletStatus.ACTIVE);
        wallet.setUserId(userId);
        walletRepository.save(wallet);

        CreateWalletResponse walletResponse = new CreateWalletResponse();
        walletResponse.setWalletId(wallet.getWalletId());
        walletResponse.setBalance(wallet.getAvailableBalance());
        walletResponse.setStatus(wallet.getStatus());
        return walletResponse;
    }

    @Override
    public void credit(Long walletId, BigDecimal amount) {
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new WalletNotFoundException("Wallet does not exists with this walletId"));
        wallet.creditAvailableBalance(amount);
        wallet.updateTotalBalance(wallet.getAvailableBalance().add(wallet.getReservedBalance()));
        walletRepository.save(wallet);
    }
    
    public void validateBalance(Long walletId, BigDecimal amount){
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new WalletNotFoundException("Wallet does not exists with this walletId"));
        if(wallet.getAvailableBalance().compareTo(amount) < 0){
            throw new InsufficientFundsException("Insufficient balance in the wallet");
        }
        reserveBalance(wallet, amount);
    }
    
    private void reserveBalance(Wallet wallet, BigDecimal amount){
        wallet.debitAvailableBalance(amount);
        wallet.creditReservedBalance(amount);
        wallet.updateTotalBalance(wallet.getAvailableBalance().add(wallet.getReservedBalance()));
    }

    public void releaseReserveBalance(Long walletId, BigDecimal amount){
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new WalletNotFoundException("Wallet does not exists with this walletId"));

        wallet.debitReservedBalance(amount);
        wallet.creditAvailableBalance(amount);
        wallet.updateTotalBalance(wallet.getAvailableBalance().add(wallet.getReservedBalance()));
    }

    @Override
    public void debit(Long walletId, BigDecimal amount, boolean isCompensation){
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new WalletNotFoundException("Wallet does not exists with this walletId"));
        if(isCompensation){
            wallet.debitAvailableBalance(amount);
        }else{
            wallet.debitReservedBalance(amount);
        }
        wallet.updateTotalBalance(wallet.getAvailableBalance().add(wallet.getReservedBalance()));
        walletRepository.save(wallet);
    }

    @Override
    public Optional<Wallet> getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId);
    }

    @Override
    public GetWalletBalanceResponse getWalletBalance(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
            .orElseThrow(() -> new WalletNotFoundException("Wallet does not exists with this walletId"));
        GetWalletBalanceResponse getWalletBalanceResponse = new GetWalletBalanceResponse();
        getWalletBalanceResponse.setAvailableBalance(wallet.getAvailableBalance());
        getWalletBalanceResponse.setReserveBalance(wallet.getReservedBalance());
        getWalletBalanceResponse.setTotalBalance(wallet.getTotalBalance());
        return getWalletBalanceResponse;
    }

    
}

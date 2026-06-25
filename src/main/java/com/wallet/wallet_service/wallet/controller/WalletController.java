package com.wallet.wallet_service.wallet.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.wallet_service.wallet.dto.CreateWalletResponse;
import com.wallet.wallet_service.wallet.dto.GetWalletBalanceResponse;
import com.wallet.wallet_service.wallet.service.WalletService;

@RestController
@RequestMapping("/wallets")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService){
        this.walletService = walletService;
    }
    
    @PostMapping("/")
    public ResponseEntity<CreateWalletResponse> createWallet(@AuthenticationPrincipal String userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(walletService.createWallet(Long.valueOf(userId)));
    }

    @GetMapping("/get-wallet-balance")
    public ResponseEntity<GetWalletBalanceResponse> getWalletBalance(@AuthenticationPrincipal String userId){
        return ResponseEntity.status(HttpStatus.OK).body(walletService.getWalletBalance(Long.valueOf(userId)));
    }
}

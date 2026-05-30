package com.wallet.wallet_service.wallet.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wallet.wallet_service.wallet.enums.WalletStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class CreateWalletResponse {
    
    @JsonProperty("wallet_id")
    Long walletId;

    BigDecimal balance;

    WalletStatus status;
}

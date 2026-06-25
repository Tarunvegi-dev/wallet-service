package com.wallet.wallet_service.wallet.dto;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetWalletBalanceResponse {
    
    BigDecimal totalBalance;

    BigDecimal availableBalance;

    BigDecimal reserveBalance;
}

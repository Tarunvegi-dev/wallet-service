package com.wallet.wallet_service.transaction.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wallet.wallet_service.transaction.enums.PaymentMode;
import com.wallet.wallet_service.transaction.enums.TransactionStatus;
import com.wallet.wallet_service.transaction.enums.TransactionType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetTransactionResponse {
    
    @JsonProperty("transaction-id")
    long transactionId;

    BigDecimal amount;

    @JsonProperty("payment-mode")
    PaymentMode paymentMode;

    @JsonProperty("reference-transaction-id")
    Long referenceTransactionId;

    @JsonProperty("transcation-type")
    TransactionType transactionType;

    @JsonProperty("transaction-status")
    TransactionStatus transactionStatus;

}

package com.wallet.wallet_service.payment.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wallet.wallet_service.transaction.enums.TransactionStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {
    
    @JsonProperty("transaction-id")
    Long transactionId;

    @JsonProperty("payment-status")
    TransactionStatus paymentStatus;
}

package com.wallet.wallet_service.payment.client.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wallet.wallet_service.transaction.enums.PaymentMode;
import com.wallet.wallet_service.transaction.enums.TransactionType;

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
public class PaymentRequest {
    @JsonProperty("transaction-id")
    Long transactionId;

    BigDecimal amount;

    @JsonProperty("payment-mode")
    PaymentMode paymentMode;

    @JsonProperty("transaction-type")
    TransactionType transactionType;

}

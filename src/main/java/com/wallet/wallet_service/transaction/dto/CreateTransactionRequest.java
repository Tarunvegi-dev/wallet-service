package com.wallet.wallet_service.transaction.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wallet.wallet_service.transaction.enums.PaymentMode;
import com.wallet.wallet_service.transaction.enums.TransactionType;
import com.wallet.wallet_service.transaction.validation.annotation.ValidTransactionRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ValidTransactionRequest
public class CreateTransactionRequest {
    @JsonProperty("amount")
    BigDecimal amount;

    @NotNull
    @JsonProperty("transaction-type")
    TransactionType transactionType;

    @JsonProperty("payment-mode")
    PaymentMode paymentMode;

    @JsonProperty("reference-transaction-id")
    Long referenceTransactionId;
}

enum CounterPartyType {
    WALLET,
    EXTERNAL
}

@FieldDefaults(level = AccessLevel.PRIVATE)
class CounterParty {
    @NotBlank
    @JsonProperty("type")
    CounterPartyType type;

    @JsonProperty("external_reference")
    String externalReference;
}

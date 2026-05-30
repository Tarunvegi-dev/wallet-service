package com.wallet.wallet_service.transaction.dto;





import com.fasterxml.jackson.annotation.JsonProperty;
import com.wallet.wallet_service.transaction.enums.TransactionStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateTransactionResponse {
    @JsonProperty("transaction-id")
    Long transactionid;

    @JsonProperty("transaction-status")
    TransactionStatus transactionStatus;
}

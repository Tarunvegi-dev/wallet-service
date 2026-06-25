package com.wallet.wallet_service.transaction.model;

import java.math.BigDecimal;
import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.wallet.wallet_service.transaction.enums.PaymentMode;
import com.wallet.wallet_service.transaction.enums.TransactionStatus;
import com.wallet.wallet_service.transaction.enums.TransactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "transaction_id", unique = true)
    Long transactionId;

    @Column(nullable = false,  precision = 19, scale = 2)
    BigDecimal amount;

    @Column(name = "sender_wallet_id")
    Long senderWalletId;

    @Column(name = "receiver_wallet_id")
    Long receiverWalletId;

    @Column(name = "payment_mode")
    @Enumerated(EnumType.STRING)
    PaymentMode paymentMode;

    @Column(name = "external_reference")
    String externalReference;

    @Column(name = "reference_transaction_id")
    Long referenceTransactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    TransactionType transactionType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status")
    TransactionStatus transactionStatus;

    @CreationTimestamp
    @Column(nullable = false, updatable = false, name = "created_at")
    Instant createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    Instant updatedAt;

    @Column(unique = true, name = "idempotency_key")
    String idempotencyKey;
}

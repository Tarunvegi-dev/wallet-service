package com.wallet.wallet_service.wallet.model;

import java.math.BigDecimal;
import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.wallet.wallet_service.wallet.enums.WalletStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "wallets")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Wallet {
    
    @Id
    @Column(nullable = false, unique = true, name = "wallet_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long walletId;

    @Column(nullable = false, precision = 19, scale = 2)
    BigDecimal balance;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    WalletStatus status;

    @Column(nullable = false, unique = true, name = "user_id")
    Long userId;

    @Column(nullable = false, name = "created_at", updatable = false)
    @CreationTimestamp
    Instant createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    Instant updatedAt;

    @Version
    Long version;

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void debit(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }
}

package com.wallet.wallet_service.transaction.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wallet.wallet_service.transaction.enums.OutboxStatus;
import com.wallet.wallet_service.transaction.model.OutboxEvent;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID>{

    List<OutboxEvent> findByStatus(OutboxStatus outboxStatus);
    
}

package com.wallet.wallet_service.transaction.service;

import java.time.Instant;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wallet.wallet_service.payment.client.dto.CompensatePaymentRequest;
import com.wallet.wallet_service.payment.config.RabbitConfig;
import com.wallet.wallet_service.transaction.enums.OutboxStatus;
import com.wallet.wallet_service.transaction.model.OutboxEvent;
import com.wallet.wallet_service.transaction.repository.OutboxRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents(){
        List<OutboxEvent> outboxEvents = outboxRepository.findByStatus(OutboxStatus.PENDING);
        for(OutboxEvent event: outboxEvents){
            try {
                CompensatePaymentRequest compensatePaymentRequest = objectMapper.readValue(event.getPayload(), CompensatePaymentRequest.class);
                rabbitTemplate.convertAndSend(RabbitConfig.COMPENSATION_QUEUE, compensatePaymentRequest);

                event.setStatus(OutboxStatus.PUBLISHED);
                event.setUpdatedAt(Instant.now());
                log.info("Outbox event published {}", event.getUuid(), event.getTransactionId());
            } catch (Exception e) {
                log.error("Failed to publish outbox event {}", event.getUuid(), e);
            }
        }
    }
}

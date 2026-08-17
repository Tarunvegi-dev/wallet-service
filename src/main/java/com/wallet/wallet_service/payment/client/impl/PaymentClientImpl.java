package com.wallet.wallet_service.payment.client.impl;

import org.slf4j.MDC;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import com.wallet.wallet_service.payment.client.PaymentClient;
import com.wallet.wallet_service.payment.client.dto.PaymentRequest;
import com.wallet.wallet_service.payment.client.dto.PaymentResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentClientImpl implements PaymentClient{
    private final RestClient restClient;

    public PaymentClientImpl(RestClient restClient){
        this.restClient = restClient;
    }

    @Recover
    public PaymentResponse recover(ResourceAccessException ex, PaymentRequest paymentRequest){
        log.error("Payment failed after all retry attempts transaction ={}", paymentRequest.getTransactionId(), ex);
        throw ex;
    }

    @Override
    @Retryable(
        retryFor = { ResourceAccessException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        return restClient
        .post()
        .uri("/payments/process")
        .header("X-Correlation-Id", MDC.get("correlationId"))
        .body(paymentRequest)
        .retrieve()
        .body(PaymentResponse.class);
    }
    
}

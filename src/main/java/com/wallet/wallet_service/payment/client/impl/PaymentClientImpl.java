package com.wallet.wallet_service.payment.client.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.wallet.wallet_service.payment.client.PaymentClient;
import com.wallet.wallet_service.payment.client.dto.PaymentRequest;
import com.wallet.wallet_service.payment.client.dto.PaymentResponse;

@Service
public class PaymentClientImpl implements PaymentClient{
    private final RestClient restClient;

    public PaymentClientImpl(RestClient restClient){
        this.restClient = restClient;
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        return restClient
        .post()
        .uri("/payments/process")
        .body(paymentRequest)
        .retrieve()
        .body(PaymentResponse.class);
    }
    
}

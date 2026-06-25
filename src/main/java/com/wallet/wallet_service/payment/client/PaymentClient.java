package com.wallet.wallet_service.payment.client;

import com.wallet.wallet_service.payment.client.dto.PaymentRequest;
import com.wallet.wallet_service.payment.client.dto.PaymentResponse;

public interface PaymentClient {

    public PaymentResponse processPayment(PaymentRequest paymentRequest);
}

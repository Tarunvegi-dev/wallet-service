package com.wallet.wallet_service.payment.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class PaymentConfig {

    private final String baseUrl;

    public PaymentConfig(@Value("${payment.service.url}") String baseUrl){
        this.baseUrl = baseUrl;
    }
    
    @Bean
    public RestClient restClient() {
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofSeconds(10));

        return RestClient.builder()
            .baseUrl(baseUrl)
            .requestFactory(factory)
            .build();
}
}

package com.wallet.wallet_service.payment.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RabbitConfig {
    public static final String COMPENSATION_QUEUE = "compensation-requested";

    @Bean
    Queue compensationQueue(){
        return new Queue(COMPENSATION_QUEUE);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

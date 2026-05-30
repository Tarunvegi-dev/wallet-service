package com.wallet.wallet_service.transaction.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.wallet.wallet_service.transaction.validation.validator.TransactionRequestValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TransactionRequestValidator.class)
public @interface ValidTransactionRequest {
    String message() default "Invalid transaction request";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
package com.wallet.wallet_service.transaction.validation.validator;

import com.wallet.wallet_service.transaction.dto.CreateTransactionRequest;
import com.wallet.wallet_service.transaction.enums.TransactionType;
import com.wallet.wallet_service.transaction.validation.annotation.ValidTransactionRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TransactionRequestValidator implements ConstraintValidator<ValidTransactionRequest, CreateTransactionRequest>{

    @Override
    public boolean isValid(CreateTransactionRequest request, ConstraintValidatorContext context) {
        if(request.getTransactionType() == TransactionType.REFUND && request.getReferenceTransactionId() == null){
            return false;
        }
        if(request.getReferenceTransactionId() != null && (request.getAmount() != null || request.getPaymentMode() != null)){
            return false;
        }
        if(request.getReferenceTransactionId() == null && (request.getAmount() == null || request.getPaymentMode() == null)){
            return false;
        }
        return true;
    }
       
}

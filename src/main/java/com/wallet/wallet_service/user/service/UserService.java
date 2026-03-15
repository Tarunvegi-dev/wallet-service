package com.wallet.wallet_service.user.service;

import com.wallet.wallet_service.user.model.SignupRequestDTO;
import com.wallet.wallet_service.user.model.SignupResponseDTO;

public interface UserService
{
    SignupResponseDTO signup(SignupRequestDTO signupRequest);
}
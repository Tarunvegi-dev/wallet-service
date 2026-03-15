package com.wallet.wallet_service.user.service;

import com.wallet.wallet_service.user.dto.LoginRequest;
import com.wallet.wallet_service.user.dto.LoginResponse;
import com.wallet.wallet_service.user.dto.SignupRequest;
import com.wallet.wallet_service.user.dto.SignupResponse;

public interface UserService
{
    SignupResponse signup(SignupRequest signupRequest);

    LoginResponse login(LoginRequest loginRequest);
}
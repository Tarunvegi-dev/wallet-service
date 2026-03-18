package com.wallet.wallet_service.user.service;

import com.wallet.wallet_service.user.dto.ForgotPasswordRequest;
import com.wallet.wallet_service.user.dto.ForgotPasswordResponse;
import com.wallet.wallet_service.user.dto.LoginRequest;
import com.wallet.wallet_service.user.dto.LoginResponse;
import com.wallet.wallet_service.user.dto.SignupRequest;
import com.wallet.wallet_service.user.dto.SignupResponse;
import com.wallet.wallet_service.user.dto.UpdatePasswordRequest;
import com.wallet.wallet_service.user.dto.UpdatePasswordResponse;
import com.wallet.wallet_service.user.dto.VerifyOTPRequest;
import com.wallet.wallet_service.user.dto.VerifyOTPResponse;

public interface UserService
{
    SignupResponse signup(SignupRequest signupRequest);

    LoginResponse login(LoginRequest loginRequest);

    ForgotPasswordResponse requestOTP(ForgotPasswordRequest forgotPasswordRequest);

    VerifyOTPResponse verifyOTP(VerifyOTPRequest verifyOTPRequest);

    UpdatePasswordResponse updatePassword(UpdatePasswordRequest updatePasswordRequest);
}
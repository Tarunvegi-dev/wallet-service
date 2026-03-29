package com.wallet.wallet_service.user.service;

import java.util.Map;
import java.util.Optional;

import com.wallet.wallet_service.user.dto.ForgotPasswordRequest;
import com.wallet.wallet_service.user.dto.ForgotPasswordResponse;
import com.wallet.wallet_service.user.dto.LoginRequest;
import com.wallet.wallet_service.user.dto.LoginResponse;
import com.wallet.wallet_service.user.dto.ResetPasswordRequest;
import com.wallet.wallet_service.user.dto.SignupRequest;
import com.wallet.wallet_service.user.dto.SignupResponse;
import com.wallet.wallet_service.user.dto.UpdatePasswordRequest;
import com.wallet.wallet_service.user.dto.UpdatePasswordResponse;
import com.wallet.wallet_service.user.dto.UserDTO;
import com.wallet.wallet_service.user.dto.VerifyOTPRequest;
import com.wallet.wallet_service.user.dto.VerifyOTPResponse;

public interface UserService
{
    SignupResponse signup(SignupRequest signupRequest);

    LoginResponse login(LoginRequest loginRequest);

    Optional<UserDTO> getUserById(Long userId);

    Boolean resetUserPassword(Long userId, ResetPasswordRequest resetPasswordRequest);

    UserDTO updateUserById(Long userId, Map<String, Object> updates);

    ForgotPasswordResponse requestOTP(ForgotPasswordRequest forgotPasswordRequest);

    VerifyOTPResponse verifyOTP(VerifyOTPRequest verifyOTPRequest);

    UpdatePasswordResponse updatePassword(UpdatePasswordRequest updatePasswordRequest);
}
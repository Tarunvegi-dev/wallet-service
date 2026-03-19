package com.wallet.wallet_service.user.service;

import com.wallet.wallet_service.user.dto.*;
import jakarta.validation.Valid;

import java.util.Map;
import java.util.Optional;

public interface UserService
{
    SignupResponse signup(SignupRequest signupRequest);

    LoginResponse login(LoginRequest loginRequest);

    Optional<UserDTO> getUserById(Long userId);

    Boolean resetUserPassword(Long userId, ResetPasswordRequest resetPasswordRequest);

    UserDTO updateUserById(Long userId, Map<String, Object> updates);
}
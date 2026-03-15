package com.walllet.wallet_service.user.service;

import com.walllet.wallet_service.user.model.SignupRequestDTO;
import com.walllet.wallet_service.user.model.SignupResponseDTO;

public interface UserService
{
    SignupResponseDTO signup(SignupRequestDTO signupRequest);
}
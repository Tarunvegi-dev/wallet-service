package com.wallet.wallet_service.user.service.Impl;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wallet.wallet_service.common.exception.InvalidCredentialsException;
import com.wallet.wallet_service.common.exception.UserAlreadyExistsException;
import com.wallet.wallet_service.common.security.JWTService;
import com.wallet.wallet_service.user.dto.LoginRequest;
import com.wallet.wallet_service.user.dto.LoginResponse;
import com.wallet.wallet_service.user.dto.SignupRequest;
import com.wallet.wallet_service.user.dto.SignupResponse;
import com.wallet.wallet_service.user.model.User;
import com.wallet.wallet_service.user.repository.UserRepository;
import com.wallet.wallet_service.user.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService
{
    final UserRepository userRepository;
    final ModelMapper objModelMapper;
    final PasswordEncoder passwordEncoder;
    final JWTService jwtService;
    
    @Override
    public SignupResponse signup(SignupRequest signupRequest)
    {
        Boolean isExists = userRepository.existsByEmail(signupRequest.getEmail());
        if(isExists)
            throw new UserAlreadyExistsException("User already present with mail "+signupRequest.getEmail());
        User userToBeSaved = objModelMapper.map(signupRequest, User.class);
        String encodedPassword = passwordEncoder.encode(userToBeSaved.getPassword());
        userToBeSaved.setPassword(encodedPassword);
        User savedUser = userRepository.save(userToBeSaved);
        return objModelMapper.map(savedUser, SignupResponse.class);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                                 .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(jwtService.generateToken(user.getUserId()));
            loginResponse.setUserId(user.getUserId());
            return loginResponse;
        }else{
            throw new InvalidCredentialsException("Invalid email or password");
        }
                                  
    }
}

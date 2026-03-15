package com.wallet.wallet_service.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.wallet_service.user.dto.LoginRequest;
import com.wallet.wallet_service.user.dto.LoginResponse;
import com.wallet.wallet_service.user.dto.SignupRequest;
import com.wallet.wallet_service.user.dto.SignupResponse;
import com.wallet.wallet_service.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController
{
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody @Valid SignupRequest signupRequest)
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.signup(signupRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.login(loginRequest));
    }
}

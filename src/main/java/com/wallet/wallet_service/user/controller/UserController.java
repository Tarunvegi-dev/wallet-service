package com.wallet.wallet_service.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.wallet_service.user.model.SignupRequestDTO;
import com.wallet.wallet_service.user.model.SignupResponseDTO;
import com.wallet.wallet_service.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController
{
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> signup(@RequestBody @Valid SignupRequestDTO signupRequest)
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.signup(signupRequest));
    }


}

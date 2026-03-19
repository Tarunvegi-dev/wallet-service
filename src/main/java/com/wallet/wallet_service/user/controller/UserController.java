package com.wallet.wallet_service.user.controller;

import com.wallet.wallet_service.user.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wallet.wallet_service.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

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

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId)
    {
        Optional<UserDTO> objUserDTO = userService.getUserById(userId);
        return objUserDTO.map(objUserDTO1 -> ResponseEntity.ok(objUserDTO1))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{userId}/reset-password")
    public ResponseEntity<HttpStatus> resetUserPassword(@PathVariable Long userId, @RequestBody @Valid ResetPasswordRequest resetPasswordRequest)
    {
        Boolean isPasswordReset = userService.resetUserPassword(userId, resetPasswordRequest);
        if(!isPasswordReset)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{userId}/update-user")
    public ResponseEntity<UserDTO> modifyEmployeeById(@RequestBody Map<String, Object> updates, @PathVariable Long userId)
    {
        UserDTO updateduserDTO = userService.updateUserById(userId, updates);
        if(updateduserDTO !=null)
            return ResponseEntity.ok(updateduserDTO);
        return ResponseEntity.notFound().build();
    }
}

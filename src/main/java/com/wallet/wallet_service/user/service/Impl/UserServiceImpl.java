package com.wallet.wallet_service.user.service.Impl;

import com.wallet.wallet_service.common.exception.*;
import com.wallet.wallet_service.user.dto.*;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wallet.wallet_service.common.email.EmailService;
import com.wallet.wallet_service.common.exception.InvalidCredentialsException;
import com.wallet.wallet_service.common.exception.UserAlreadyExistsException;
import com.wallet.wallet_service.common.security.JWTService;
import com.wallet.wallet_service.common.security.OTPService;
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
import com.wallet.wallet_service.user.model.User;
import com.wallet.wallet_service.user.repository.UserRepository;
import com.wallet.wallet_service.user.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService
{
    final UserRepository userRepository;
    final ModelMapper objModelMapper;
    final PasswordEncoder passwordEncoder;
    final JWTService jwtService;
    final OTPService otpService;
    final EmailService emailService;

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

    @Override
    public ForgotPasswordResponse requestOTP(ForgotPasswordRequest forgotPasswordRequest) {
        String email = forgotPasswordRequest.getEmail();
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isPresent()){
            String otp = otpService.generateAndStoreOTP(email);
            emailService.SendOTP(email, otp);
        }
        return new ForgotPasswordResponse(email, "If account exists, OTP sent");
    }

    public Optional<UserDTO> getUserById(Long userId)
    {
        if(!userRepository.existsById(userId))
            throw new UserNotFoundException("User not found with ID "+userId);
        return userRepository.findById(userId)
                .map(objUser -> objModelMapper.map(objUser, UserDTO.class));
    }

    public Boolean resetUserPassword(Long userId, ResetPasswordRequest resetPasswordRequest)
    {
        if(!userRepository.existsById(userId))
            throw new UserNotFoundException("User not found with ID "+userId);
        User user = userRepository.findById(userId).orElseThrow();
        if(!passwordEncoder.matches(resetPasswordRequest.getOldPassword(), user.getPassword()))
            throw new InvalidPasswordException("Invalid Current Password!!!");
        if(resetPasswordRequest.getOldPassword().equals(resetPasswordRequest.getNewPassword()))
            throw new SamePasswordException("Current Password and New Password can not be same");
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);
        return true;
    }

    @Override
    public UserDTO updateUserById(Long userId, Map<String, Object> updates)
    {
        if(!userRepository.existsById(userId))
            throw new UserNotFoundException("User not found with ID "+userId);
        UserDTO UpdatedUserDTO = new UserDTO();
        User currentUser = userRepository.findById(userId).orElse(null);
        if(currentUser!=null)
        {
            updates.forEach((field, value) -> {
                Field fieldToBeUpdated = ReflectionUtils.findField(User.class, field);
                fieldToBeUpdated.setAccessible(true);
                ReflectionUtils.setField(fieldToBeUpdated, currentUser, value);
            });
            UpdatedUserDTO = objModelMapper.map(userRepository.save(currentUser),UserDTO.class);
        }
        return UpdatedUserDTO;
    }



    @Override
    public VerifyOTPResponse verifyOTP(VerifyOTPRequest verifyOTPRequest) {
        VerifyOTPResponse verifyOTPResponse = new VerifyOTPResponse();
        verifyOTPResponse.setEmail(verifyOTPRequest.getEmail());
        if(otpService.verifyOTP(verifyOTPRequest.getEmail(), verifyOTPRequest.getOtp())){
            verifyOTPResponse.setMesssage("OTP Verified Successfully!!");
        }else{
            verifyOTPResponse.setMesssage("Invalid OTP!");
        }
        return verifyOTPResponse;
    }

    @Override
    public UpdatePasswordResponse updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        UpdatePasswordResponse updatePasswordResponse = new UpdatePasswordResponse();
        updatePasswordResponse.setEmail(updatePasswordRequest.getEmail());
        if(otpService.isOTPVerified(updatePasswordRequest.getEmail())){
            otpService.clearOTP(updatePasswordRequest.getEmail());
            User user = userRepository.findByEmail(updatePasswordRequest.getEmail())
                                      .orElseThrow(() -> new InvalidCredentialsException("User not found with email "+updatePasswordRequest.getEmail()));
            String encodedPassword = passwordEncoder.encode(updatePasswordRequest.getNewPassword());
            user.setPassword(encodedPassword);
            userRepository.save(user);
            updatePasswordResponse.setMessage("Password updated successfully!!");
        }else{
            updatePasswordResponse.setMessage("OTP not verified or expired!!");
        }
        return updatePasswordResponse;
    }

}

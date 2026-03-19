package com.wallet.wallet_service.user.service.Impl;

import com.wallet.wallet_service.common.exception.*;
import com.wallet.wallet_service.user.dto.*;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wallet.wallet_service.common.security.JWTService;
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


}

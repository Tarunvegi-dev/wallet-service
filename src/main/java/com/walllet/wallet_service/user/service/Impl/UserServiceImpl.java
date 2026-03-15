package com.walllet.wallet_service.user.service.Impl;

import com.walllet.wallet_service.common.exception.UserAlreadyExistsException;
import com.walllet.wallet_service.user.model.SignupRequestDTO;
import com.walllet.wallet_service.user.model.SignupResponseDTO;
import com.walllet.wallet_service.user.model.User;
import com.walllet.wallet_service.user.repository.UserRepository;
import com.walllet.wallet_service.user.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService
{
    final UserRepository userRepository;
    final ModelMapper objModelMapper;
    final PasswordEncoder passwordEncoder;
    public SignupResponseDTO signup(SignupRequestDTO signupRequest)
    {
        Boolean isExists = userRepository.existsByEmail(signupRequest.getEmail());
        if(isExists)
            throw new UserAlreadyExistsException("User already present with mail "+signupRequest.getEmail());
        User userToBeSaved = objModelMapper.map(signupRequest, User.class);
        String encodedPassword = passwordEncoder.encode(userToBeSaved.getPassword());
        userToBeSaved.setPassword(encodedPassword);
        User savedUser = userRepository.save(userToBeSaved);
        return objModelMapper.map(savedUser, SignupResponseDTO.class);
    }
}

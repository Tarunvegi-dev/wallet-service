package com.wallet.wallet_service.user.service.Impl;

import com.wallet.wallet_service.common.email.EmailService;
import com.wallet.wallet_service.common.exception.InvalidCredentialsException;
import com.wallet.wallet_service.common.exception.UserAlreadyExistsException;
import com.wallet.wallet_service.common.exception.UserNotFoundException;
import com.wallet.wallet_service.common.security.JWTService;
import com.wallet.wallet_service.common.security.OTPService;
import com.wallet.wallet_service.user.dto.*;
import com.wallet.wallet_service.user.model.User;
import com.wallet.wallet_service.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserServiceImplTest
{
    @Mock
    private UserRepository userRepository;
    @Spy
    private ModelMapper objModelMapper;
    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Spy
    private JWTService jwtService;
    @Mock
    private OTPService otpService;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private User user;
    @BeforeEach
    void dataSetup()
    {
        user = User.builder()
                .userId(2L)
                .email("testUser@gmail.com")
                .password("testUser@1245")
                .name("testUser")
                .mobileNumber("789654133").build();
    }

    @Test
    void signupTestWithValidDataAndSuccessCase()
    {
        //ARRANGE
        SignupRequest signupRequest = objModelMapper.map(user, SignupRequest.class);

        //Stubbing: Telling Mockito to return user object on save() call
        when(userRepository.save(any(User.class))).thenReturn(user);

        //ACT
        SignupResponse signupResponse = userServiceImpl.signup(signupRequest);

        //ASSERT
        assertThat(signupResponse).isNotNull();
        assertThat(signupResponse.getEmail()).isEqualTo(signupRequest.getEmail());

        // Verify the save call happened and check the captured object
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();
        assertThat(capturedUser.getEmail()).isEqualTo(signupRequest.getEmail());
    }

    @Test
    void signupTestWithValidDataAndDuplicateDataCase()
    {
        //ARRANGE
        SignupRequest signupRequest = objModelMapper.map(user, SignupRequest.class);
        signupRequest.setEmail("akash@gmail.com");
        when(userRepository.existsByEmail("akash@gmail.com")).thenReturn(true);

        //ACT & ASSERT
        assertThatThrownBy(() -> userServiceImpl.signup(signupRequest))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessage("User already present with mail "+signupRequest.getEmail());

        verify(userRepository).existsByEmail(signupRequest.getEmail());
        verify(userRepository, never()).save(any());
    }

//    @Test
//    void loginTestWithValidInputAndSuccessCase()
//    {
//        //ARRANGE
//        LoginRequest loginRequest = new LoginRequest("testUser@gmail.com", "testUser@1245");
//        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
//        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
//        //ACT
//        LoginResponse loginResponse = userServiceImpl.login(loginRequest);
//
//        //ASSERT
//        verify(userRepository).findByEmail(loginRequest.getEmail());
//        assertThat(loginResponse.getToken()).isNotNull();
//    }


    @Test
    void loginTestWithValidInputAndUserNotFoundCase()
    {
        //ARRANGE
        LoginRequest loginRequest = new LoginRequest("testUser@gmail.com", "testUser@1245");
        when(userRepository.findByEmail(loginRequest.getEmail())).thenThrow(new InvalidCredentialsException("Invalid email or password"));

        //ACT & ASSERT
        assertThatThrownBy(() -> userServiceImpl.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid email or password");

        //ASSERT
        verify(userRepository).findByEmail(loginRequest.getEmail());
    }

    @Test
    void loginTestWithinValidPasswordCase()
    {
        //ARRANGE
        LoginRequest loginRequest = new LoginRequest("testUser@gmail.com", "testUser@124");
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        //ACT & ASSERT
        assertThatThrownBy(() -> userServiceImpl.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid email or password");

        //ASSERT
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), user.getPassword());
    }

    @Test
    void getUserByIdTestWithValidUserId()
    {
        //ARRANGE
        when(userRepository.existsById(any())).thenReturn(true);
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        //ACT
        Optional<UserDTO> userDTO = userServiceImpl.getUserById(user.getUserId());

        //ASSERT
        verify(userRepository).findById(user.getUserId());
        assertThat(userDTO).isNotNull();
        UserDTO fetchedUserDTO = objModelMapper.map(userDTO, UserDTO.class);
        assertThat(fetchedUserDTO.getEmail()).isEqualTo(user.getEmail());
        assertThat(fetchedUserDTO.getName()).isEqualTo(user.getName());
    }


    @Test
    void getUserByIdTestWithInvalidUserId()
    {
        //ARRANGE
        user.setUserId(5L);
        when(userRepository.existsById(5L)).thenReturn(false);

        //ACT & ASSERT
        assertThatThrownBy(() -> userServiceImpl.getUserById(user.getUserId()))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with ID "+user.getUserId());

        verify(userRepository).existsById(user.getUserId());
    }

    @Test
    void updateUserByIdTestWithValidUserId()
    {
        //ARRANGE
        Map<String, Object> updates = new HashMap<String, Object>() {
            {
                put("name", "updatedTestUser");
                put("mobileNumber", "4587963214");
            }
        };
        when(userRepository.existsById(any())).thenReturn(true);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        User updatedUser = user;
        updatedUser.setName("updatedTestUser");
        updatedUser.setMobileNumber("4587963214");
        when(userRepository.save(any())).thenReturn(updatedUser);

        //ACT
        UserDTO userDTO = userServiceImpl.updateUserById(user.getUserId(), updates);

        //ASSERT
        assertThat(userDTO).isNotNull();
        assertThat(userDTO.getName()).isEqualTo(updates.get("name"));
        assertThat(userDTO.getMobileNumber()).isEqualTo(updates.get("mobileNumber"));

        verify(userRepository).existsById(user.getUserId());
        verify(userRepository).findById(user.getUserId());
        verify(userRepository).save(any());
    }

    @Test
    void updateUserByIdTestWithInvalidUserId()
    {
        //ARRANGE
        Map<String, Object> updates = new HashMap<String, Object>() {
            {
                put("name", "updatedTestUser");
                put("mobileNumber", "4587963214");
            }
        };
        user.setUserId(5L);
        when(userRepository.existsById(user.getUserId())).thenReturn(false);

        //ACT & ASSERT
        assertThatThrownBy(() -> userServiceImpl.updateUserById(user.getUserId(), updates))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with ID "+user.getUserId());

        verify(userRepository).existsById(user.getUserId());
    }

    @Test
    void updateUserByIdTestWithValidUserIdAndInvalidData()
    {
        //ARRANGE
        Map<String, Object> updates = new HashMap<String, Object>() {
            {
                put("email", "updatedTestUser");
                put("mobileNumber", "4587963214");
            }
        };
        user.setUserId(5L);
        when(userRepository.existsById(user.getUserId())).thenReturn(true);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        //ACT & ASSERT
        assertThatThrownBy(() -> userServiceImpl.updateUserById(user.getUserId(), updates))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email or password of user can not be updated from here");

        verify(userRepository).existsById(user.getUserId());
        verify(userRepository).findById(user.getUserId());
    }

    @Test
    void verifyOTPTest()
    {

    }

    @Test
    void updatePasswordTest()
    {

    }

    @Test
    void requestOTPTest()
    {

    }

    @Test
    void resetUserPasswordTest()
    {

    }
}
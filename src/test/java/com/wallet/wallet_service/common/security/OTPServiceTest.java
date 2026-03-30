package com.wallet.wallet_service.common.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import lombok.extern.slf4j.Slf4j;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Slf4j
class OTPServiceTest {

    @InjectMocks
    private OTPService otpService;

    @Test
    void generateAndStoreOTPTest()
    {
        //ACT
        String otp = otpService.generateAndStoreOTP("testUser@gmail.com");
        log.info("Generated OTP is"+ otp);
        //ASSERT
        assertThat(otp).isNotNull();
    }

    @Test
    void verifyOTPTestWithSuccessCase()
    {
        //ARRANGE
        String email = "testUser@gmail.com";
        String otp = otpService.generateAndStoreOTP("testUser@gmail.com");
        //ACT
        Boolean isOtpVerified = otpService.verifyOTP(email, otp);

        //ASSERT
        assertThat(isOtpVerified).isTrue();
    }

    @Test
    void verifyOTPTestWithFailCase()
    {
        //ARRANGE
        String email = "testUser1@gmail.com";
        String email2 = "testUser2@gmail.com";
        String otp = otpService.generateAndStoreOTP(email);
        //ACT
        Boolean isOtpVerified = otpService.verifyOTP(email2, otp);

        //ASSERT
        assertThat(isOtpVerified).isFalse();
    }

    @Test
    void isOTPVerified() {
    }

    @Test
    void clearOTP() {
    }
}
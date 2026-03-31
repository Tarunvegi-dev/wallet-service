package com.wallet.wallet_service.common.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import lombok.extern.slf4j.Slf4j;


@ExtendWith(MockitoExtension.class)
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
package com.wallet.wallet_service.common.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;    

    public void SendOTP(String email, String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("wallet.service.dev@gmail.com");
        message.setTo(email);
        message.setSubject("Your OTP for Wallet Service");
        message.setText("Your OTP is: " + otp);
        javaMailSender.send(message);
    }
}

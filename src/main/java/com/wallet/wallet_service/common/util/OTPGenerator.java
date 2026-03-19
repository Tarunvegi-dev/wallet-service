package com.wallet.wallet_service.common.util;

import java.security.SecureRandom;

public class OTPGenerator {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateOTP(){
        return String.valueOf(secureRandom.nextInt(900000) + 100000);
    }
}

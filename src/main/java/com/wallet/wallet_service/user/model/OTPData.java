package com.wallet.wallet_service.user.model;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OTPData {
    String otp;
    Date otpExpiry;
    Boolean isVerified;
    Date verifiedExpiry;
}

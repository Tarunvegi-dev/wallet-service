package com.wallet.wallet_service.user.model;

import java.util.Date;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OTPData {
    String otp;
    Date otpExpiry;
    Boolean isVerified;
    Date verifiedExpiry;
}

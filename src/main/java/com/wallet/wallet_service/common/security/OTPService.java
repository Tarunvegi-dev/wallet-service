package com.wallet.wallet_service.common.security;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

import com.wallet.wallet_service.common.util.OTPGenerator;
import com.wallet.wallet_service.user.model.OTPData;

@Service
public class OTPService {
    private final Map<String, OTPData> otpStore = new ConcurrentHashMap<>();
    private final int expirationTime = 300000;

    public String generateAndStoreOTP(String email){
        if(otpStore.containsKey(email)){
            Date expDate = otpStore.get(email).getOtpExpiry();
            if(expDate.after(new Date())){
                return otpStore.get(email).getOtp();
            }
        }
        String otp = OTPGenerator.generateOTP();
        OTPData otpData = new OTPData();
        otpData.setOtp(otp);
        otpData.setOtpExpiry(new Date(System.currentTimeMillis()+expirationTime));
        otpData.setIsVerified(false);
        otpStore.put(email, otpData);
        return otp;
    }

    public Boolean verifyOTP(String email, String OTP){
        if(otpStore.containsKey(email)){
            OTPData otpData = otpStore.get(email);
            if(otpData.getIsVerified()) return true;
            Date expDate = otpData.getOtpExpiry();
            if(OTP.equals(otpData.getOtp()) && expDate.after(new Date())){
                otpData.setIsVerified(true);
                otpData.setVerifiedExpiry(new Date(System.currentTimeMillis()+expirationTime));
                return true;
            }
            if(expDate.before(new Date())){
                otpStore.remove(email);
            }
            return false;
        }
        return false;
    }

    public Boolean isOTPVerified(String email){
        if(otpStore.containsKey(email)){
            OTPData otpData = otpStore.get(email);
            if(otpData.getIsVerified() && otpData.getVerifiedExpiry().after(new Date())){
                return true;
            }
        }
        return false;
    }

    public void clearOTP(String email){
        otpStore.remove(email);
    }
}

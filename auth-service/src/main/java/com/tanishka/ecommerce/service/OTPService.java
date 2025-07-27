package com.tanishka.ecommerce.service;

public interface OTPService {

	String sendOtp(String email);
    boolean verifyOtp(String email, String otp);
    String resetPassword(String email, String newPassword);

}

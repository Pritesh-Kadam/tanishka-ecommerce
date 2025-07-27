package com.tanishka.ecommerce.serviceimpl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tanishka.ecommerce.entity.User;
import com.tanishka.ecommerce.event.ResetPasswordEvent;
import com.tanishka.ecommerce.repository.IUserRepository;
import com.tanishka.ecommerce.service.OTPService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {

	private final IUserRepository userRepository;
    private final JavaMailSender mailSender;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
	private MailServiceImpl emailService;

    @Autowired
    private KafkaProducerService kafkaProducerService;
    private final Map<String, String> otpStore = new HashMap<>();
    private final Map<String, LocalDateTime> otpExpiry = new HashMap<>();

    @Override
    public String sendOtp(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Email not registered.");
        }

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        otpStore.put(email, otp);
        otpExpiry.put(email, LocalDateTime.now().plusMinutes(5));

        // Send OTP via email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP for Password Reset");
        message.setText("Your OTP is: " + otp + " (valid for 5 minutes)");
        mailSender.send(message);

        return "OTP sent to email.";
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        if (!otpStore.containsKey(email)) return false;
        if (!otpStore.get(email).equals(otp)) return false;

        LocalDateTime expiry = otpExpiry.get(email);
        if (expiry == null || expiry.isBefore(LocalDateTime.now())) return false;

        return true;
    }
    
    @Override
    public String resetPassword(String email, String newPassword) {
        if (!otpStore.containsKey(email) || otpExpiry.get(email).isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired or not found.");
        }

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found."));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        String body = "Hi " + user.getName() + ",\n\nYour Password For Tanishka Collection is reset.\nYour updated Password is :"+newPassword;
	    emailService.sendEmail(user.getEmail(), "Password Reset", body);

	    ResetPasswordEvent event = new ResetPasswordEvent(
	    		user.getUserId(),
	    		user.getEmail()
	    		);
	    kafkaProducerService.sendResetPasswordEvent(event);
        // Clear used OTP
        otpStore.remove(email);
        otpExpiry.remove(email);

        return "Password reset successfully.";
    }
   
}

package com.tanishka.ecommerce.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tanishka.ecommerce.service.OTPService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/otp")
public class OTPController {

	@Autowired
    private OTPService otpService;

	@Operation(
		    summary = "Send OTP to user's email",
		    description = "Sends a one-time password (OTP) to the provided email address."
		)
		@ApiResponses(value = {
		    @ApiResponse(responseCode = "200", description = "OTP sent successfully"),
		    @ApiResponse(responseCode = "500", description = "Internal server error")
		})
	 @PostMapping("/send-otp")
	    public ResponseEntity<String> sendOtp(@RequestParam String email) {
	        try {
	            String result = otpService.sendOtp(email);
	            return ResponseEntity.ok(result);
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
	        }
	    }

	@Operation(
		    summary = "Verify OTP",
		    description = "Verifies the OTP sent to the user's email."
		)
		@ApiResponses(value = {
		    @ApiResponse(responseCode = "200", description = "OTP verified successfully"),
		    @ApiResponse(responseCode = "400", description = "Invalid or expired OTP"),
		    @ApiResponse(responseCode = "500", description = "Internal server error")
		})
	    @PostMapping("/verify-otp")
	    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
	        try {
	            boolean valid = otpService.verifyOtp(email, otp);
	            return valid ?
	                ResponseEntity.ok("OTP verified.") :
	                ResponseEntity.badRequest().body("Invalid or expired OTP.");
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
	        }
	    }

	@Operation(
		    summary = "Reset password",
		    description = "Resets the user's password using email after successful OTP verification."
		)
		@ApiResponses(value = {
		    @ApiResponse(responseCode = "200", description = "Password reset successfully"),
		    @ApiResponse(responseCode = "500", description = "Internal server error")
		})
	    @PostMapping("/reset-password")
	    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
	        try {
	            String result = otpService.resetPassword(email, newPassword);
	            return ResponseEntity.ok(result);
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
	        }
	    }

}

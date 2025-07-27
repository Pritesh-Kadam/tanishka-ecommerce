package com.tanishka.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestDTO {
	@NotBlank(message = "Name Is Required")
	@Size(min = 2, max = 50, message = "Name Must Be Between 2 to 50 characters")
	private String name;

	@NotBlank(message = "Email Is Required")
    @Email(message ="Invalid Email Format")
	private String email;

	@NotBlank(message = "Password Is Required")
    @Size(min = 6, max = 15, message = "Password Must Be Between 6 to 15 characters")
	@Pattern(
		    regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).{6,15}$",
		    message = "Password must include at least one letter, one number, and one special character"
		)
	private String password;
	
	@NotBlank(message = "Mobile Number Is Required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
	private String mobileNo;

	private String role;

}

package com.tanishka.ecommerce.serviceimpl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tanishka.ecommerce.dto.AuthRequestDTO;
import com.tanishka.ecommerce.dto.AuthResponseDTO;
import com.tanishka.ecommerce.dto.UserRequestDTO;
import com.tanishka.ecommerce.dto.UserResponseDTO;
import com.tanishka.ecommerce.entity.Role;
import com.tanishka.ecommerce.entity.User;
import com.tanishka.ecommerce.event.UserCreatedEvent;
import com.tanishka.ecommerce.exception.CustomException;
import com.tanishka.ecommerce.repository.IUserRepository;
import com.tanishka.ecommerce.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private IUserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtServiceImpl jwtService;
	
	@Autowired
	private MailServiceImpl emailService;
	
	@Autowired
	private KafkaProducerService kafkaProducerService;

	@Override
	public AuthResponseDTO login(AuthRequestDTO request) {
		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

		if (!Boolean.TRUE.equals(user.getIsActive())) {
			throw new DisabledException("User account is deactivated");
		}

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new BadCredentialsException("Invalid password");
		}

		String role = user.getRole().name();
		String accessToken = jwtService.generateAccessToken(user.getEmail(), role);
		String refreshToken = jwtService.generateRefreshToken(user.getEmail(),role);

		return new AuthResponseDTO(accessToken, refreshToken);
	}

	@Override
	public AuthResponseDTO refreshToken(String refreshToken) {
		try {
			if (!jwtService.isTokenValid(refreshToken)) {
				throw new IllegalArgumentException("Invalid refresh token");
			}

			String email = jwtService.extractUsername(refreshToken);
			User user = userRepository.findByEmail(email)
	                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

	        String role = user.getRole().name(); // Get role as String
	        String newAccessToken = jwtService.generateAccessToken(email, role);

			return new AuthResponseDTO(newAccessToken, refreshToken);
		} catch (IllegalArgumentException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new RuntimeException("Failed to refresh token: " + ex.getMessage(), ex);
		}
	}

	@Override
	public UserResponseDTO registerUser(UserRequestDTO request) {
		try {
			Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
			if (existingUser.isPresent()) {
				throw new CustomException("Email already in use.");
			}

			User user = new User();
			user.setName(request.getName());
			user.setEmail(request.getEmail());
			user.setPassword(passwordEncoder.encode(request.getPassword()));
			if (userRepository.existsByMobileNo(request.getMobileNo())) {
			    throw new IllegalArgumentException("Mobile number already exists.");
			}
			user.setMobileNo(request.getMobileNo());

			String roleStr = request.getRole();
			Role role;
			try {
				role = Role.valueOf(roleStr != null ? roleStr.toUpperCase() : "USER");
			} catch (IllegalArgumentException e) {
				throw new CustomException("Invalid role provided");
			}

			user.setRole(role);
			user.setCreatedAt(LocalDateTime.now());
			user.setIsActive(true);

			User savedUser = userRepository.save(user);
			// Send welcome or verification email
		    String body = "Hi " + user.getName() + ",\n\nWelcome to Tanishka Collection Your account has been Sucessfully Created.";
		    emailService.sendEmail(user.getEmail(), "Welcome to Our System", body);
		    UserCreatedEvent event = new UserCreatedEvent(
					savedUser.getUserId(),
		    		savedUser.getName(),
					savedUser.getEmail()
					);
		    kafkaProducerService.sendUserCreatedEvent(event);
		    return new UserResponseDTO(savedUser);
		} catch (Exception e) {
			throw new CustomException("Registration failed: " + e.getMessage());
		}
	}

	@Override
	public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
		try {
			Page<User> userPage = userRepository.findAll(pageable);
			return userPage.map(UserResponseDTO::new);
		} catch (Exception e) {
			// You can also log this exception using a logger
			throw new RuntimeException("Error fetching users: " + e.getMessage(), e);
		}
	}

	@Override
	public ResponseEntity<?> addUser(UserRequestDTO request) {
		String roleStr = request.getRole();
		if (roleStr == null) {
			return ResponseEntity.badRequest().body("Role is required.");
		}

		// Prevent adding USER role from this endpoint
		if ("USER".equalsIgnoreCase(roleStr)) {
			return ResponseEntity.badRequest().body("Cannot add USER role from this endpoint.");
		}

		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
		}

		Role role;
		try {
			role = Role.valueOf(roleStr.toUpperCase());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body("Invalid role provided.");
		}

		User user = new User();
		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(role);
		if (userRepository.existsByMobileNo(request.getMobileNo())) {
		    throw new IllegalArgumentException("Mobile number already exists.");
		}
		user.setMobileNo(request.getMobileNo());
		user.setIsActive(true);
		user.setCreatedAt(LocalDateTime.now());
		userRepository.save(user);
		// Send welcome or verification email
	    String body = "Hi " + user.getName() + ",\n\nWelcome to Tanishka Collection Your account has been Sucessfully Added.";
	    emailService.sendEmail(user.getEmail(), "Welcome to Our System", body);
	    UserCreatedEvent event = new UserCreatedEvent(
	    		user.getUserId(),
	    		user.getName(),
	    		user.getEmail());
	    kafkaProducerService.sendUserCreatedEvent(event);
		return ResponseEntity.ok("User added successfully");
	}


}

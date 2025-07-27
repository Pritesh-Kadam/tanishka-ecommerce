package com.tanishka.ecommerce.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.tanishka.ecommerce.dto.AuthRequestDTO;
import com.tanishka.ecommerce.dto.AuthResponseDTO;
import com.tanishka.ecommerce.dto.UserRequestDTO;
import com.tanishka.ecommerce.dto.UserResponseDTO;
@Service
public interface AuthService {

	public AuthResponseDTO login(AuthRequestDTO request);
	
	public AuthResponseDTO refreshToken(String refreshToken);
	
    public UserResponseDTO registerUser(UserRequestDTO request);

    Page<UserResponseDTO> getAllUsers(Pageable pageable);

    ResponseEntity<?> addUser(UserRequestDTO request);

}

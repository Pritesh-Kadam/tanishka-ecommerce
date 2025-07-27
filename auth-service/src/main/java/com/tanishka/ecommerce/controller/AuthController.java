package com.tanishka.ecommerce.controller;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tanishka.ecommerce.config.JwtFilter;
import com.tanishka.ecommerce.dto.AuthRequestDTO;
import com.tanishka.ecommerce.dto.AuthResponseDTO;
import com.tanishka.ecommerce.dto.UserRequestDTO;
import com.tanishka.ecommerce.dto.UserResponseDTO;
import com.tanishka.ecommerce.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtFilter jwtFilter;
    
        
    @Operation(summary = "Login with email and password", description = "Returns JWT access and refresh tokens")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid email or password"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
    	    summary = "Logout user",
    	    description = "Invalidates the JWT by blacklisting the token from the request header."
    	)
    	@ApiResponses(value = {
    	    @ApiResponse(responseCode = "200", description = "Logged out successfully"),
    	    @ApiResponse(responseCode = "400", description = "Bad request - No token found in request")
    	})
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("No token found in request");
        }

        String token = authHeader.substring(7); // Remove "Bearer "
        jwtFilter.blacklistToken(token);

        return ResponseEntity.ok("Logged out successfully");
    }
    
    @Operation(
    	    summary = "Refresh access token",
    	    description = "Generates a new access token using the provided refresh token. Returns an updated JWT if the refresh token is valid."
    	)
    	@ApiResponses(value = {
    	    @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
    	    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or expired refresh token"),
    	    @ApiResponse(responseCode = "500", description = "Internal server error")
    	})
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
        try {
            AuthResponseDTO response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to refresh token."));
        }
    }
    
    @Operation(
    	    summary = "Register a new user",
    	    description = "Registers a user into the system. Typically used by the user themselves."
    	)
    	@ApiResponses(value = {
    	    @ApiResponse(responseCode = "200", description = "User registered successfully"),
    	    @ApiResponse(responseCode = "400", description = "Validation failed"),
    	    @ApiResponse(responseCode = "500", description = "Internal server error")
    	})
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequestDTO request) {
       
            UserResponseDTO createdUser = authService.registerUser(request);
            return ResponseEntity.ok(createdUser);
        
    }
    
    @Operation(
    	    summary = "Get All Users",
    	    description = "Fetches a paginated list of all registered users. Only accessible to users with ADMIN role."
    	)
    	@ApiResponses(value = {
    	    @ApiResponse(responseCode = "200", description = "Users fetched successfully",
    	                 content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))),
    	    @ApiResponse(responseCode = "500", description = "Internal server error",
    	                 content = @Content(mediaType = "application/json"))
    	})
    @GetMapping("/getAll")
//  @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers(
    	@RequestParam(defaultValue = "0")int page,
    	@RequestParam(defaultValue = "10")int size,
    	@RequestParam(defaultValue = "id") String sortBy
    	) {
        try {
            Page<UserResponseDTO> users = authService.getAllUsers(PageRequest.of(page, size, Sort.by(sortBy)));
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Failed to retrieve users: " + e.getMessage());
        }
    }
    
    @Operation(
            summary = "Add a new user",
            description = "Allows an admin to create a new user in the system."
        )
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or user role is 'USER'", 
                         content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Server error")
        })
    @PostMapping("/addUser")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addUser(@Valid @RequestBody UserRequestDTO request) {
        return authService.addUser(request);
    }

}

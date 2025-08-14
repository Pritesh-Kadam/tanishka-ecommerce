package com.tanishka.ecommerce.serviceimpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tanishka.ecommerce.dto.UserRequestDTO;
import com.tanishka.ecommerce.dto.UserResponseDTO;
import com.tanishka.ecommerce.entity.Role;
import com.tanishka.ecommerce.entity.User;
import com.tanishka.ecommerce.event.UserCreatedEvent;
import com.tanishka.ecommerce.exception.CustomException;
import com.tanishka.ecommerce.repository.IUserRepository;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MailServiceImpl mailService; // or interface if AuthServiceImpl depends on interface

    @Mock
    private UserEventProducer userEventProducer;

    @Mock
    private KafkaProducerService kafkaProducerService;
    
    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    @Captor
    private ArgumentCaptor<User> userCaptor;
    
    @Captor private ArgumentCaptor<UserCreatedEvent> eventCaptor;


    @Test
    @DisplayName("registerUser: successful registration should save user, send email and emit event")
    void registerUser_success() {
        // Arrange
        UserRequestDTO req = new UserRequestDTO();
        req.setName("Pritesh Kadam");
        req.setEmail("collectionoftanishka@gmail.com");
        req.setMobileNo("9999999999");
        req.setPassword("Pass");
        req.setRole("USER");

        when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.empty());
        when(userRepository.existsByMobileNo(req.getMobileNo())).thenReturn(false);
        when(passwordEncoder.encode("Pass")).thenReturn("hashedpwd");

        User savedUser = new User();
        savedUser.setUserId(1L);
        savedUser.setRole(Role.USER);
        savedUser.setCreatedAt(LocalDateTime.now());
        savedUser.setEmail(req.getEmail());
        savedUser.setIsActive(true);
        savedUser.setName(req.getName());
        savedUser.setMobileNo(req.getMobileNo());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserResponseDTO response = authServiceImpl.registerUser(req);

        // Assert
        assertNotNull(response);
        assertEquals(req.getEmail(), response.getEmail());

        verify(passwordEncoder).encode("Pass");
        verify(mailService).sendEmail(eq(req.getEmail()), anyString(), anyString());
        verify(kafkaProducerService).sendUserCreatedEvent(any()); // <-- use the correct mock

        // Optionally verify what was saved
        verify(userRepository).save(userCaptor.capture());
        User toSave = userCaptor.getValue();
        assertEquals(req.getEmail(), toSave.getEmail());
        assertEquals("hashedpwd", toSave.getPassword()); // assuming service sets encoded password
        assertEquals(req.getMobileNo(), toSave.getMobileNo());
        assertEquals(Role.USER, toSave.getRole());
    }

    @Test
    @DisplayName("registerUser: when email exists should throw CustomException")
    void registerdUser_emailExists_throws() {
        UserRequestDTO req = new UserRequestDTO();
        req.setEmail("collectionoftanishka@gmail.com");

        when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.of(new User()));

        CustomException ex = assertThrows(CustomException.class, () -> authServiceImpl.registerUser(req));
        assertTrue(ex.getMessage().toLowerCase().contains("email already in use"));
    }

    @Test
    @DisplayName("registerUser: when mobile already exists should throw IllegalArgumentException")
    void registeredUser_mobileExists_throws() {
        UserRequestDTO req = new UserRequestDTO();
        req.setMobileNo("9999999999");
        req.setEmail("collectionoftanishka@gmail.com");
        req.setRole("USER");

        when(userRepository.findByEmail(req.getEmail())).thenReturn(Optional.empty());
        when(userRepository.existsByMobileNo(req.getMobileNo())).thenReturn(true);

        CustomException ex = assertThrows(CustomException.class,
                () -> authServiceImpl.registerUser(req));
        assertTrue(ex.getMessage().toLowerCase().contains("mobile number already exists"));
    }
}

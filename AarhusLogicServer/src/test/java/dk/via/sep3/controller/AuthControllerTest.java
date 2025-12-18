package dk.via.sep3.controller;

import dk.via.sep3.exceptionHandler.BusinessRuleViolationException;
import dk.via.sep3.mapper.userMapper.UserMapper;
import dk.via.sep3.application.domain.User;
import dk.via.sep3.application.services.register.RegisterService;
import dk.via.sep3.application.services.login.LoginService;
import dk.via.sep3.security.JwtUtil;
import dk.via.sep3.DTOs.auth.RegisterResponseDTO;
import dk.via.sep3.DTOs.registration.RegistrationDTO;
import dk.via.sep3.DTOs.user.UserDTO;
import dk.via.sep3.DTOs.login.LoginRequestDTO;
import dk.via.sep3.DTOs.login.LoginResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthController
 * Tests registration functionality with various scenarios
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private RegisterService registerService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private LoginService loginService;

    @InjectMocks
    private AuthController authController;

    private RegistrationDTO validRegistrationDTO;
    private User validDomainUser;
    private User registeredUser;
    private RegisterResponseDTO registerResponseDTO;

    @BeforeEach
    void setUp() {
        // Setup valid registration DTO
        validRegistrationDTO = new RegistrationDTO();
        validRegistrationDTO.setFullName("John Doe");
        validRegistrationDTO.setEmail("john.doe@example.com");
        validRegistrationDTO.setPhoneNumber("12345678");
        validRegistrationDTO.setUsername("johndoe");
        validRegistrationDTO.setPassword("SecurePass123");

        // Setup domain user (before registration)
        validDomainUser = new User();
        validDomainUser.setName("John Doe");
        validDomainUser.setEmail("john.doe@example.com");
        validDomainUser.setPhoneNumber("12345678");
        validDomainUser.setUsername("johndoe");
        validDomainUser.setPassword("SecurePass123");

        // Setup registered user (after registration with hashed password)
        registeredUser = new User();
        registeredUser.setName("John Doe");
        registeredUser.setEmail("john.doe@example.com");
        registeredUser.setPhoneNumber("12345678");
        registeredUser.setUsername("johndoe");
        registeredUser.setPassword("$2a$10$hashedPassword");
        registeredUser.setRole("Reader");

        // Setup RegisterResponseDTO
        registerResponseDTO = new RegisterResponseDTO();
        registerResponseDTO.setUsername("johndoe");
        registerResponseDTO.setName("John Doe");
        registerResponseDTO.setEmail("john.doe@example.com");
        registerResponseDTO.setPhoneNumber("12345678");
        registerResponseDTO.setRole("Reader");
    }

    @Test
    @DisplayName("Should successfully register user when all validations pass")
    void testRegister_Success() {
        // Arrange
        when(userMapper.mapRegistrationDTOToDomain(validRegistrationDTO)).thenReturn(validDomainUser);
        when(registerService.register(validDomainUser)).thenReturn(registeredUser);
        when(userMapper.mapDomainToRegisterResponseDTO(registeredUser)).thenReturn(registerResponseDTO);

        // Act
        ResponseEntity<RegisterResponseDTO> response = authController.register(validRegistrationDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("johndoe", response.getBody().getUsername());
        assertEquals("John Doe", response.getBody().getName());
        assertEquals("john.doe@example.com", response.getBody().getEmail());
        assertEquals("12345678", response.getBody().getPhoneNumber());
        assertEquals("Reader", response.getBody().getRole());

        // Verify interactions
        verify(userMapper, times(1)).mapRegistrationDTOToDomain(validRegistrationDTO);
        verify(registerService, times(1)).register(validDomainUser);
        verify(userMapper, times(1)).mapDomainToRegisterResponseDTO(registeredUser);
    }

    @Test
    @DisplayName("Should throw exception when username validation fails")
    void testRegister_InvalidUsername() {
        // Arrange
        when(userMapper.mapRegistrationDTOToDomain(validRegistrationDTO)).thenReturn(validDomainUser);
        when(registerService.register(validDomainUser))
                .thenThrow(new BusinessRuleViolationException("Username must be at least 3 characters"));

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> authController.register(validRegistrationDTO)
        );

        assertEquals("Username must be at least 3 characters", exception.getMessage());

        // Verify registration was attempted
        verify(userMapper, times(1)).mapRegistrationDTOToDomain(validRegistrationDTO);
        verify(registerService, times(1)).register(validDomainUser);
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void testRegister_UsernameAlreadyExists() {
        // Arrange
        when(userMapper.mapRegistrationDTOToDomain(validRegistrationDTO)).thenReturn(validDomainUser);
        when(registerService.register(validDomainUser))
                .thenThrow(new BusinessRuleViolationException("Username already in use"));

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> authController.register(validRegistrationDTO)
        );

        assertEquals("Username already in use", exception.getMessage());

        // Verify interactions
        verify(userMapper, times(1)).mapRegistrationDTOToDomain(validRegistrationDTO);
        verify(registerService, times(1)).register(validDomainUser);
        verify(userMapper, never()).mapDomainToRegisterResponseDTO(any());
    }

    @Test
    @DisplayName("Should throw exception when email is invalid")
    void testRegister_InvalidEmail() {
        // Arrange
        when(userMapper.mapRegistrationDTOToDomain(validRegistrationDTO)).thenReturn(validDomainUser);
        when(registerService.register(validDomainUser))
                .thenThrow(new BusinessRuleViolationException("Invalid email format"));

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> authController.register(validRegistrationDTO)
        );

        assertEquals("Invalid email format", exception.getMessage());

        // Verify registration was attempted
        verify(userMapper, times(1)).mapRegistrationDTOToDomain(validRegistrationDTO);
        verify(registerService, times(1)).register(validDomainUser);
    }

    @Test
    @DisplayName("Should handle registration service errors gracefully")
    void testRegister_ServiceError() {
        // Arrange
        when(userMapper.mapRegistrationDTOToDomain(validRegistrationDTO)).thenReturn(validDomainUser);
        when(registerService.register(validDomainUser))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authController.register(validRegistrationDTO)
        );

        assertEquals("Database connection failed", exception.getMessage());

        // Verify interactions
        verify(userMapper, times(1)).mapRegistrationDTOToDomain(validRegistrationDTO);
        verify(registerService, times(1)).register(validDomainUser);
    }

    @Test
    @DisplayName("Should map registered user correctly to response DTO")
    void testRegister_CorrectMapping() {
        // Arrange
        when(userMapper.mapRegistrationDTOToDomain(validRegistrationDTO)).thenReturn(validDomainUser);
        when(registerService.register(validDomainUser)).thenReturn(registeredUser);
        when(userMapper.mapDomainToRegisterResponseDTO(registeredUser)).thenReturn(registerResponseDTO);

        // Act
        ResponseEntity<RegisterResponseDTO> response = authController.register(validRegistrationDTO);

        // Assert - verify password is not exposed
        assertNotNull(response.getBody());
        assertNotEquals("$2a$10$hashedPassword", response.getBody().getUsername());
        assertNotEquals("SecurePass123", response.getBody().getUsername());

        // Verify correct user data is mapped
        assertEquals(registerResponseDTO.getUsername(), response.getBody().getUsername());
        assertEquals(registerResponseDTO.getName(), response.getBody().getName());
        assertEquals(registerResponseDTO.getEmail(), response.getBody().getEmail());
        assertEquals(registerResponseDTO.getPhoneNumber(), response.getBody().getPhoneNumber());
        assertEquals(registerResponseDTO.getRole(), response.getBody().getRole());
    }

    @Test
    @DisplayName("Should login successfully and return a token")
    void testLogin_Success() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("johndoe");
        loginRequest.setPassword("SecurePass123");

        when(userMapper.mapLoginRequestToDomain(loginRequest)).thenReturn(validDomainUser);
        when(loginService.login(validDomainUser)).thenReturn(registeredUser);
        when(jwtUtil.generateToken(registeredUser.getUsername(), registeredUser.getRole())).thenReturn("mock-token");

        // Act
        ResponseEntity<LoginResponseDTO> response = authController.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("mock-token", response.getBody().getToken());
        assertEquals("johndoe", response.getBody().getUsername());

        // Verify interactions
        verify(userMapper, times(1)).mapLoginRequestToDomain(loginRequest);
        verify(loginService, times(1)).login(validDomainUser);
        verify(jwtUtil, times(1)).generateToken(registeredUser.getUsername(), registeredUser.getRole());
    }

    @Test
    @DisplayName("Should throw exception when login credentials are invalid")
    void testLogin_InvalidCredentials() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("johndoe");
        loginRequest.setPassword("WrongPassword");

        when(userMapper.mapLoginRequestToDomain(loginRequest)).thenReturn(validDomainUser);
        when(loginService.login(validDomainUser)).thenThrow(new IllegalArgumentException("Invalid username or password"));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> authController.login(loginRequest));
        assertEquals("Invalid username or password", exception.getMessage());

        // Verify interactions
        verify(userMapper, times(1)).mapLoginRequestToDomain(loginRequest);
        verify(loginService, times(1)).login(validDomainUser);
        verify(jwtUtil, never()).generateToken(any(), any());
    }
}


package dk.via.sep3.controller;

import dk.via.sep3.exceptionHandler.BusinessRuleViolationException;
import dk.via.sep3.mapper.userMapper.UserMapper;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.model.register.RegisterService;
import dk.via.sep3.model.utils.validation.Validator;
import dk.via.sep3.security.JwtUtil;
import dk.via.sep3.shared.auth.AuthResponseDTO;
import dk.via.sep3.shared.registration.RegistrationDTO;
import dk.via.sep3.shared.user.UserDTO;
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
    private Validator<String> passwordValidator;

    @InjectMocks
    private AuthController authController;

    private RegistrationDTO validRegistrationDTO;
    private User validDomainUser;
    private User registeredUser;
    private UserDTO userDTO;

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

        // Setup UserDTO
        userDTO = new UserDTO();
        userDTO.setFullName("John Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPhoneNumber("12345678");
        userDTO.setUsername("johndoe");
        userDTO.setRole("Reader");
    }

    @Test
    @DisplayName("Should successfully register user when all validations pass")
    void testRegister_Success() {
        // Arrange
        when(userMapper.mapRegistrationDTOToDomain(validRegistrationDTO)).thenReturn(validDomainUser);
        when(registerService.register(validDomainUser)).thenReturn(registeredUser);
        when(userMapper.mapDomainToUserDTO(registeredUser)).thenReturn(userDTO);
        doNothing().when(passwordValidator).validate(anyString());

        // Act
        ResponseEntity<AuthResponseDTO> response = authController.register(validRegistrationDTO);

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
        verify(passwordValidator, times(1)).validate("SecurePass123");
        verify(userMapper, times(1)).mapRegistrationDTOToDomain(validRegistrationDTO);
        verify(registerService, times(1)).register(validDomainUser);
        verify(userMapper, times(1)).mapDomainToUserDTO(registeredUser);
    }

    @Test
    @DisplayName("Should throw BusinessRuleViolationException when registration DTO is null")
    void testRegister_NullDTO() {
        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> authController.register(null)
        );

        assertEquals("Registration cannot be null", exception.getMessage());

        // Verify no interactions with dependencies
        verify(passwordValidator, never()).validate(anyString());
        verify(userMapper, never()).mapRegistrationDTOToDomain(any());
        verify(registerService, never()).register(any());
    }

    @Test
    @DisplayName("Should throw exception when password validation fails")
    void testRegister_InvalidPassword() {
        // Arrange
        doThrow(new BusinessRuleViolationException("Password must be at least 8 characters"))
                .when(passwordValidator).validate(anyString());

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> authController.register(validRegistrationDTO)
        );

        assertEquals("Password must be at least 8 characters", exception.getMessage());

        // Verify password validator was called but registration was not
        verify(passwordValidator, times(1)).validate("SecurePass123");
        verify(userMapper, never()).mapRegistrationDTOToDomain(any());
        verify(registerService, never()).register(any());
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void testRegister_UsernameAlreadyExists() {
        // Arrange
        when(userMapper.mapRegistrationDTOToDomain(validRegistrationDTO)).thenReturn(validDomainUser);
        when(registerService.register(validDomainUser))
                .thenThrow(new BusinessRuleViolationException("Username already in use"));
        doNothing().when(passwordValidator).validate(anyString());

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> authController.register(validRegistrationDTO)
        );

        assertEquals("Username already in use", exception.getMessage());

        // Verify interactions
        verify(passwordValidator, times(1)).validate("SecurePass123");
        verify(userMapper, times(1)).mapRegistrationDTOToDomain(validRegistrationDTO);
        verify(registerService, times(1)).register(validDomainUser);
        verify(userMapper, never()).mapDomainToUserDTO(any());
    }

    @Test
    @DisplayName("Should throw exception when email is invalid")
    void testRegister_InvalidEmail() {
        // Arrange
        doThrow(new BusinessRuleViolationException("Invalid email format"))
                .when(passwordValidator).validate(anyString());

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> authController.register(validRegistrationDTO)
        );

        assertEquals("Invalid email format", exception.getMessage());

        // Verify only password validator was called
        verify(passwordValidator, times(1)).validate("SecurePass123");
        verify(registerService, never()).register(any());
    }

    @Test
    @DisplayName("Should handle registration service errors gracefully")
    void testRegister_ServiceError() {
        // Arrange
        when(userMapper.mapRegistrationDTOToDomain(validRegistrationDTO)).thenReturn(validDomainUser);
        when(registerService.register(validDomainUser))
                .thenThrow(new RuntimeException("Database connection failed"));
        doNothing().when(passwordValidator).validate(anyString());

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authController.register(validRegistrationDTO)
        );

        assertEquals("Database connection failed", exception.getMessage());

        // Verify interactions
        verify(passwordValidator, times(1)).validate("SecurePass123");
        verify(userMapper, times(1)).mapRegistrationDTOToDomain(validRegistrationDTO);
        verify(registerService, times(1)).register(validDomainUser);
    }

    @Test
    @DisplayName("Should map registered user correctly to response DTO")
    void testRegister_CorrectMapping() {
        // Arrange
        when(userMapper.mapRegistrationDTOToDomain(validRegistrationDTO)).thenReturn(validDomainUser);
        when(registerService.register(validDomainUser)).thenReturn(registeredUser);
        when(userMapper.mapDomainToUserDTO(registeredUser)).thenReturn(userDTO);
        doNothing().when(passwordValidator).validate(anyString());

        // Act
        ResponseEntity<AuthResponseDTO> response = authController.register(validRegistrationDTO);

        // Assert - verify password is not exposed
        assertNotNull(response.getBody());
        assertNotEquals("$2a$10$hashedPassword", response.getBody().getUsername());
        assertNotEquals("SecurePass123", response.getBody().getUsername());

        // Verify correct user data is mapped
        assertEquals(userDTO.getUsername(), response.getBody().getUsername());
        assertEquals(userDTO.getName(), response.getBody().getName());
        assertEquals(userDTO.getEmail(), response.getBody().getEmail());
        assertEquals(userDTO.getPhoneNumber(), response.getBody().getPhoneNumber());
        assertEquals(userDTO.getRole(), response.getBody().getRole());
    }
}


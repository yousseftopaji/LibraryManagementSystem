package dk.via.sep3.model.register;

import dk.via.sep3.exceptionHandler.BusinessRuleViolationException;
import dk.via.sep3.exceptionHandler.GrpcCommunicationException;
import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.model.utils.validation.RegistrationValidator;
import dk.via.sep3.security.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RegisterServiceImpl
 * Tests user registration business logic including validation, password hashing, and persistence
 */
@ExtendWith(MockitoExtension.class)
class RegisterServiceImplTest {

    @Mock
    private UserGrpcService userGrpcService;

    @Mock
    private PasswordService passwordService;

    @Mock
    private RegistrationValidator registrationValidator;

    @InjectMocks
    private RegisterServiceImpl registerService;

    private User validUser;
    private User createdUser;

    @BeforeEach
    void setUp() {
        // Setup valid user
        validUser = new User();
        validUser.setName("John Doe");
        validUser.setEmail("john.doe@example.com");
        validUser.setPhoneNumber("12345678");
        validUser.setUsername("johndoe");
        validUser.setPassword("PlainPassword123");

        // Setup created user with hashed password
        createdUser = new User();
        createdUser.setName("John Doe");
        createdUser.setEmail("john.doe@example.com");
        createdUser.setPhoneNumber("12345678");
        createdUser.setUsername("johndoe");
        createdUser.setPassword("$2a$10$hashedPassword");
        createdUser.setRole("Reader");
    }

    @Test
    @DisplayName("Should successfully register user with hashed password")
    void testRegister_Success() {
        // Arrange
        String hashedPassword = "$2a$10$hashedPassword";
        doNothing().when(registrationValidator).validate(validUser);
        when(passwordService.hash("PlainPassword123")).thenReturn(hashedPassword);
        when(userGrpcService.createUser(any(User.class))).thenReturn(createdUser);

        // Act
        User result = registerService.register(validUser);

        // Assert
        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("12345678", result.getPhoneNumber());
        assertEquals("Reader", result.getRole());
        assertEquals(hashedPassword, result.getPassword());

        // Verify interactions
        verify(registrationValidator, times(1)).validate(validUser);
        verify(passwordService, times(1)).hash("PlainPassword123");
        verify(userGrpcService, times(1)).createUser(any(User.class));
    }

    @Test
    @DisplayName("Should throw BusinessRuleViolationException when user is null")
    void testRegister_NullUser() {
        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> registerService.register(null)
        );

        assertEquals("Registration cannot be null", exception.getMessage());

        // Verify no interactions
        verify(registrationValidator, never()).validate(any());
        verify(passwordService, never()).hash(anyString());
        verify(userGrpcService, never()).createUser(any());
    }

    @Test
    @DisplayName("Should throw exception when validation fails")
    void testRegister_ValidationFails() {
        // Arrange
        doThrow(new BusinessRuleViolationException("Invalid email format"))
                .when(registrationValidator).validate(validUser);

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> registerService.register(validUser)
        );

        assertEquals("Invalid email format", exception.getMessage());

        // Verify validator was called but password was not hashed
        verify(registrationValidator, times(1)).validate(validUser);
        verify(passwordService, never()).hash(anyString());
        verify(userGrpcService, never()).createUser(any());
    }

    @Test
    @DisplayName("Should hash password before persisting user")
    void testRegister_PasswordIsHashed() {
        // Arrange
        String hashedPassword = "$2a$10$someHashedPassword";
        doNothing().when(registrationValidator).validate(validUser);
        when(passwordService.hash("PlainPassword123")).thenReturn(hashedPassword);
        when(userGrpcService.createUser(any(User.class))).thenReturn(createdUser);

        // Act
        registerService.register(validUser);

        // Assert - verify the user object was modified before being sent to gRPC
        assertEquals(hashedPassword, validUser.getPassword());
        verify(passwordService, times(1)).hash("PlainPassword123");
    }

    @Test
    @DisplayName("Should set role to Reader")
    void testRegister_SetsReaderRole() {
        // Arrange
        doNothing().when(registrationValidator).validate(validUser);
        when(passwordService.hash(anyString())).thenReturn("$2a$10$hashedPassword");
        when(userGrpcService.createUser(any(User.class))).thenReturn(createdUser);

        // Act
        registerService.register(validUser);

        // Assert
        assertEquals("Reader", validUser.getRole());
        verify(userGrpcService, times(1)).createUser(any(User.class));
    }

    @Test
    @DisplayName("Should throw GrpcCommunicationException when userGrpcService returns null")
    void testRegister_GrpcReturnsNull() {
        // Arrange
        doNothing().when(registrationValidator).validate(validUser);
        when(passwordService.hash(anyString())).thenReturn("$2a$10$hashedPassword");
        when(userGrpcService.createUser(any(User.class))).thenReturn(null);

        // Act & Assert
        GrpcCommunicationException exception = assertThrows(
                GrpcCommunicationException.class,
                () -> registerService.register(validUser)
        );

        assertEquals("Failed to create user", exception.getMessage());

        // Verify all steps were attempted
        verify(registrationValidator, times(1)).validate(validUser);
        verify(passwordService, times(1)).hash("PlainPassword123");
        verify(userGrpcService, times(1)).createUser(any(User.class));
    }

    @Test
    @DisplayName("Should throw GrpcCommunicationException when userGrpcService throws exception")
    void testRegister_GrpcThrowsException() {
        // Arrange
        doNothing().when(registrationValidator).validate(validUser);
        when(passwordService.hash(anyString())).thenReturn("$2a$10$hashedPassword");
        when(userGrpcService.createUser(any(User.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        GrpcCommunicationException exception = assertThrows(
                GrpcCommunicationException.class,
                () -> registerService.register(validUser)
        );

        assertEquals("Failed to create user", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("Database connection failed", exception.getCause().getMessage());

        // Verify interactions
        verify(registrationValidator, times(1)).validate(validUser);
        verify(passwordService, times(1)).hash("PlainPassword123");
        verify(userGrpcService, times(1)).createUser(any(User.class));
    }

    @Test
    @DisplayName("Should call validator with correct user object")
    void testRegister_ValidatorCalledWithCorrectUser() {
        // Arrange
        doNothing().when(registrationValidator).validate(validUser);
        when(passwordService.hash(anyString())).thenReturn("$2a$10$hashedPassword");
        when(userGrpcService.createUser(any(User.class))).thenReturn(createdUser);

        // Act
        registerService.register(validUser);

        // Assert - verify validator was called with the original user object
        verify(registrationValidator, times(1)).validate(validUser);
    }

    @Test
    @DisplayName("Should return created user from gRPC service")
    void testRegister_ReturnsCreatedUser() {
        // Arrange
        doNothing().when(registrationValidator).validate(validUser);
        when(passwordService.hash(anyString())).thenReturn("$2a$10$hashedPassword");
        when(userGrpcService.createUser(any(User.class))).thenReturn(createdUser);

        // Act
        User result = registerService.register(validUser);

        // Assert
        assertSame(createdUser, result);
        assertEquals("Reader", result.getRole());
        assertEquals("$2a$10$hashedPassword", result.getPassword());
    }
}


package dk.via.sep3.model.auth;

import dk.via.sep3.exceptionHandler.BusinessRuleViolationException;
import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.model.utils.validation.LoginValidator;
import dk.via.sep3.security.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthServiceImpl
 * Tests authentication business logic
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserGrpcService userGrpcService;

    @Mock
    private PasswordService passwordService;

    @Mock
    private LoginValidator loginValidator;

    @InjectMocks
    private AuthServiceImpl authService;

    private User loginRequest;
    private User storedUser;

    @BeforeEach
    void setUp() {
        // Setup login request
        loginRequest = new User();
        loginRequest.setUsername("johndoe");
        loginRequest.setPassword("PlainPassword123");

        // Setup stored user (from database)
        storedUser = new User();
        storedUser.setUsername("johndoe");
        storedUser.setPassword("$2a$10$hashedPassword");
        storedUser.setRole("Reader");
        storedUser.setName("John Doe");
        storedUser.setEmail("john@example.com");
    }

    @Test
    @DisplayName("Should successfully authenticate user with correct credentials")
    void testLogin_Success() {
        // Arrange
        doNothing().when(loginValidator).validate(loginRequest);
        when(userGrpcService.getUserByUsername("johndoe")).thenReturn(storedUser);
        when(passwordService.matches("PlainPassword123", "$2a$10$hashedPassword")).thenReturn(true);

        // Act
        User result = authService.login(loginRequest);

        // Assert
        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        assertEquals("Reader", result.getRole());
        assertEquals("John Doe", result.getName());

        verify(loginValidator, times(1)).validate(loginRequest);
        verify(userGrpcService, times(1)).getUserByUsername("johndoe");
        verify(passwordService, times(1)).matches("PlainPassword123", "$2a$10$hashedPassword");
    }

    @Test
    @DisplayName("Should throw exception when user is null")
    void testLogin_NullUser() {
        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> authService.login(null)
        );

        assertEquals("User credentials be null", exception.getMessage());
        verify(loginValidator, never()).validate(any());
        verify(userGrpcService, never()).getUserByUsername(anyString());
        verify(passwordService, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when username is null")
    void testLogin_NullUsername() {
        // Arrange
        loginRequest.setUsername(null);
        doNothing().when(loginValidator).validate(loginRequest);

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals("Username and Password must be provided", exception.getMessage());
        verify(loginValidator, times(1)).validate(loginRequest);
        verify(userGrpcService, never()).getUserByUsername(anyString());
    }

    @Test
    @DisplayName("Should throw exception when password is null")
    void testLogin_NullPassword() {
        // Arrange
        loginRequest.setPassword(null);
        doNothing().when(loginValidator).validate(loginRequest);

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals("Username and Password must be provided", exception.getMessage());
        verify(loginValidator, times(1)).validate(loginRequest);
        verify(userGrpcService, never()).getUserByUsername(anyString());
    }

    @Test
    @DisplayName("Should throw exception when username is empty")
    void testLogin_EmptyUsername() {
        // Arrange
        loginRequest.setUsername("");
        doNothing().when(loginValidator).validate(loginRequest);

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals("Username and Password must be provided", exception.getMessage());
        verify(loginValidator, times(1)).validate(loginRequest);
        verify(userGrpcService, never()).getUserByUsername(anyString());
    }

    @Test
    @DisplayName("Should throw exception when password is empty")
    void testLogin_EmptyPassword() {
        // Arrange
        loginRequest.setPassword("");
        doNothing().when(loginValidator).validate(loginRequest);

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals("Username and Password must be provided", exception.getMessage());
        verify(loginValidator, times(1)).validate(loginRequest);
        verify(userGrpcService, never()).getUserByUsername(anyString());
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testLogin_UserNotFound() {
        // Arrange
        doNothing().when(loginValidator).validate(loginRequest);
        when(userGrpcService.getUserByUsername("johndoe")).thenReturn(null);

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals("User not found", exception.getMessage());
        verify(loginValidator, times(1)).validate(loginRequest);
        verify(userGrpcService, times(1)).getUserByUsername("johndoe");
        verify(passwordService, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when password is incorrect")
    void testLogin_WrongPassword() {
        // Arrange
        doNothing().when(loginValidator).validate(loginRequest);
        when(userGrpcService.getUserByUsername("johndoe")).thenReturn(storedUser);
        when(passwordService.matches("PlainPassword123", "$2a$10$hashedPassword")).thenReturn(false);

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals("Wrong password", exception.getMessage());
        verify(loginValidator, times(1)).validate(loginRequest);
        verify(userGrpcService, times(1)).getUserByUsername("johndoe");
        verify(passwordService, times(1)).matches("PlainPassword123", "$2a$10$hashedPassword");
    }

    @Test
    @DisplayName("Should call loginValidator before authentication")
    void testLogin_ValidatorCalled() {
        // Arrange
        doNothing().when(loginValidator).validate(loginRequest);
        when(userGrpcService.getUserByUsername("johndoe")).thenReturn(storedUser);
        when(passwordService.matches(anyString(), anyString())).thenReturn(true);

        // Act
        authService.login(loginRequest);

        // Assert
        verify(loginValidator, times(1)).validate(loginRequest);
    }

    @Test
    @DisplayName("Should throw exception when validator fails")
    void testLogin_ValidatorThrowsException() {
        // Arrange
        doThrow(new BusinessRuleViolationException("Validation failed"))
                .when(loginValidator).validate(loginRequest);

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals("Validation failed", exception.getMessage());
        verify(loginValidator, times(1)).validate(loginRequest);
        verify(userGrpcService, never()).getUserByUsername(anyString());
    }

    @Test
    @DisplayName("Should return user with all fields from database")
    void testLogin_ReturnsCompleteUser() {
        // Arrange
        doNothing().when(loginValidator).validate(loginRequest);
        when(userGrpcService.getUserByUsername("johndoe")).thenReturn(storedUser);
        when(passwordService.matches("PlainPassword123", "$2a$10$hashedPassword")).thenReturn(true);

        // Act
        User result = authService.login(loginRequest);

        // Assert
        assertEquals(storedUser.getUsername(), result.getUsername());
        assertEquals(storedUser.getPassword(), result.getPassword());
        assertEquals(storedUser.getRole(), result.getRole());
        assertEquals(storedUser.getName(), result.getName());
        assertEquals(storedUser.getEmail(), result.getEmail());
    }

    @Test
    @DisplayName("Should handle gRPC service exceptions")
    void testLogin_GrpcServiceException() {
        // Arrange
        doNothing().when(loginValidator).validate(loginRequest);
        when(userGrpcService.getUserByUsername("johndoe"))
                .thenThrow(new RuntimeException("gRPC connection failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals("gRPC connection failed", exception.getMessage());
        verify(loginValidator, times(1)).validate(loginRequest);
        verify(userGrpcService, times(1)).getUserByUsername("johndoe");
        verify(passwordService, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should handle passwordService exceptions")
    void testLogin_PasswordServiceException() {
        // Arrange
        doNothing().when(loginValidator).validate(loginRequest);
        when(userGrpcService.getUserByUsername("johndoe")).thenReturn(storedUser);
        when(passwordService.matches("PlainPassword123", "$2a$10$hashedPassword"))
                .thenThrow(new RuntimeException("Password matching error"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> authService.login(loginRequest)
        );

        assertEquals("Password matching error", exception.getMessage());
        verify(loginValidator, times(1)).validate(loginRequest);
        verify(userGrpcService, times(1)).getUserByUsername("johndoe");
        verify(passwordService, times(1)).matches("PlainPassword123", "$2a$10$hashedPassword");
    }

    @Test
    @DisplayName("Should authenticate users with different roles")
    void testLogin_DifferentRoles() {
        // Test Librarian
        User librarianUser = new User();
        librarianUser.setUsername("librarian");
        librarianUser.setPassword("$2a$10$hashedPassword");
        librarianUser.setRole("Librarian");

        User librarianRequest = new User();
        librarianRequest.setUsername("librarian");
        librarianRequest.setPassword("librarianPass");

        doNothing().when(loginValidator).validate(librarianRequest);
        when(userGrpcService.getUserByUsername("librarian")).thenReturn(librarianUser);
        when(passwordService.matches("librarianPass", "$2a$10$hashedPassword")).thenReturn(true);

        User result = authService.login(librarianRequest);
        assertEquals("Librarian", result.getRole());

        // Test Admin
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword("$2a$10$hashedPassword");
        adminUser.setRole("Admin");

        User adminRequest = new User();
        adminRequest.setUsername("admin");
        adminRequest.setPassword("adminPass");

        doNothing().when(loginValidator).validate(adminRequest);
        when(userGrpcService.getUserByUsername("admin")).thenReturn(adminUser);
        when(passwordService.matches("adminPass", "$2a$10$hashedPassword")).thenReturn(true);

        result = authService.login(adminRequest);
        assertEquals("Admin", result.getRole());
    }
}


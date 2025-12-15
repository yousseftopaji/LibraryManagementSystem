package dk.via.sep3.controller;

import dk.via.sep3.mapper.userMapper.UserMapper;
import dk.via.sep3.model.auth.AuthService;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.security.JwtUtil;
import dk.via.sep3.shared.login.LoginRequestDTO;
import dk.via.sep3.shared.login.LoginResponseDTO;
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
 * Unit tests for LoginController
 * Tests login functionality
 */
@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private LoginController loginController;

    private LoginRequestDTO loginRequest;
    private User domainUser;
    private User authenticatedUser;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        // Setup login request DTO
        loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("johndoe");
        loginRequest.setPassword("SecurePass123");

        // Setup domain user (before authentication)
        domainUser = new User();
        domainUser.setUsername("johndoe");
        domainUser.setPassword("SecurePass123");

        // Setup authenticated user (after authentication)
        authenticatedUser = new User();
        authenticatedUser.setUsername("johndoe");
        authenticatedUser.setPassword("$2a$10$hashedPassword");
        authenticatedUser.setRole("Reader");
        authenticatedUser.setName("John Doe");
        authenticatedUser.setEmail("john@example.com");

        // Setup JWT token
        jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huZG9lIiwicm9sZSI6IlJlYWRlciJ9.signature";
    }

    @Test
    @DisplayName("Should successfully login user and return token")
    void testLogin_Success() {
        // Arrange
        when(userMapper.mapLoginRequestToDomain(loginRequest)).thenReturn(domainUser);
        when(authService.login(domainUser)).thenReturn(authenticatedUser);
        when(jwtUtil.generateToken("johndoe", "Reader")).thenReturn(jwtToken);

        // Act
        ResponseEntity<LoginResponseDTO> response = loginController.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(jwtToken, response.getBody().getToken());
        assertEquals("johndoe", response.getBody().getUsername());

        // Verify interactions
        verify(userMapper, times(1)).mapLoginRequestToDomain(loginRequest);
        verify(authService, times(1)).login(domainUser);
        verify(jwtUtil, times(1)).generateToken("johndoe", "Reader");
    }

    @Test
    @DisplayName("Should throw exception when credentials are invalid")
    void testLogin_InvalidCredentials() {
        // Arrange
        when(userMapper.mapLoginRequestToDomain(loginRequest)).thenReturn(domainUser);
        when(authService.login(domainUser))
                .thenThrow(new IllegalArgumentException("Invalid username or password"));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loginController.login(loginRequest)
        );

        assertEquals("Invalid username or password", exception.getMessage());

        // Verify mapper and authService were called, but not jwtUtil
        verify(userMapper, times(1)).mapLoginRequestToDomain(loginRequest);
        verify(authService, times(1)).login(domainUser);
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    @DisplayName("Should generate JWT token with correct username and role")
    void testLogin_TokenGeneration() {
        // Arrange
        when(userMapper.mapLoginRequestToDomain(loginRequest)).thenReturn(domainUser);
        when(authService.login(domainUser)).thenReturn(authenticatedUser);
        when(jwtUtil.generateToken("johndoe", "Reader")).thenReturn(jwtToken);

        // Act
        ResponseEntity<LoginResponseDTO> response = loginController.login(loginRequest);

        // Assert
        verify(jwtUtil, times(1)).generateToken("johndoe", "Reader");
        assertNotNull(response.getBody());
        assertEquals(jwtToken, response.getBody().getToken());
    }

    @Test
    @DisplayName("Should handle null login request")
    void testLogin_NullRequest() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            loginController.login(null);
        });

        verify(userMapper, never()).mapLoginRequestToDomain(any());
        verify(authService, never()).login(any());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    @DisplayName("Should handle authentication service errors")
    void testLogin_AuthServiceError() {
        // Arrange
        when(userMapper.mapLoginRequestToDomain(loginRequest)).thenReturn(domainUser);
        when(authService.login(domainUser))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> loginController.login(loginRequest)
        );

        assertEquals("Database connection failed", exception.getMessage());
        verify(userMapper, times(1)).mapLoginRequestToDomain(loginRequest);
        verify(authService, times(1)).login(domainUser);
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    @DisplayName("Should return response with correct HTTP status")
    void testLogin_HttpStatus() {
        // Arrange
        when(userMapper.mapLoginRequestToDomain(loginRequest)).thenReturn(domainUser);
        when(authService.login(domainUser)).thenReturn(authenticatedUser);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn(jwtToken);

        // Act
        ResponseEntity<LoginResponseDTO> response = loginController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should map login request to domain correctly")
    void testLogin_MappingInvocation() {
        // Arrange
        when(userMapper.mapLoginRequestToDomain(loginRequest)).thenReturn(domainUser);
        when(authService.login(domainUser)).thenReturn(authenticatedUser);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn(jwtToken);

        // Act
        loginController.login(loginRequest);

        // Assert - verify mapper was called with correct argument
        verify(userMapper, times(1)).mapLoginRequestToDomain(loginRequest);
    }

    @Test
    @DisplayName("Should call authService with mapped domain user")
    void testLogin_AuthServiceInvocation() {
        // Arrange
        when(userMapper.mapLoginRequestToDomain(loginRequest)).thenReturn(domainUser);
        when(authService.login(domainUser)).thenReturn(authenticatedUser);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn(jwtToken);

        // Act
        loginController.login(loginRequest);

        // Assert - verify authService was called with correct user
        verify(authService, times(1)).login(domainUser);
    }

    @Test
    @DisplayName("Should handle different user roles")
    void testLogin_DifferentRoles() {
        // Arrange - Test with Librarian role
        User librarianUser = new User();
        librarianUser.setUsername("librarian");
        librarianUser.setRole("Librarian");

        when(userMapper.mapLoginRequestToDomain(loginRequest)).thenReturn(domainUser);
        when(authService.login(domainUser)).thenReturn(librarianUser);
        when(jwtUtil.generateToken("librarian", "Librarian")).thenReturn("librarian.jwt.token");

        // Act
        ResponseEntity<LoginResponseDTO> response = loginController.login(loginRequest);

        // Assert
        verify(jwtUtil, times(1)).generateToken("librarian", "Librarian");
        assertNotNull(response.getBody());
        assertEquals("librarian.jwt.token", response.getBody().getToken());
        assertEquals("librarian", response.getBody().getUsername());
    }

    @Test
    @DisplayName("Should create LoginResponseDTO with token and username")
    void testLogin_ResponseDTOCreation() {
        // Arrange
        when(userMapper.mapLoginRequestToDomain(loginRequest)).thenReturn(domainUser);
        when(authService.login(domainUser)).thenReturn(authenticatedUser);
        when(jwtUtil.generateToken("johndoe", "Reader")).thenReturn(jwtToken);

        // Act
        ResponseEntity<LoginResponseDTO> response = loginController.login(loginRequest);

        // Assert
        assertNotNull(response.getBody());
        LoginResponseDTO responseDTO = response.getBody();
        assertNotNull(responseDTO.getToken());
        assertNotNull(responseDTO.getUsername());
        assertEquals(jwtToken, responseDTO.getToken());
        assertEquals("johndoe", responseDTO.getUsername());
    }
}


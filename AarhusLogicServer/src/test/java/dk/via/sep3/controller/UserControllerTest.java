package dk.via.sep3.controller;

import dk.via.sep3.security.IJwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private IJwtTokenProvider jwtProvider;

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(jwtProvider);
    }

    @Test
    @DisplayName("Should return username when valid token provided")
    void testMe_ValidToken() {
        // Arrange
        String token = "validToken123";
        String authHeader = "Bearer " + token;
        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(jwtProvider.getUsernameFromToken(token)).thenReturn("testuser");

        // Act
        ResponseEntity<?> response = userController.me(authHeader);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testuser", response.getBody());
        verify(jwtProvider).validateToken(token);
        verify(jwtProvider).getUsernameFromToken(token);
    }

    @Test
    @DisplayName("Should return UNAUTHORIZED when no Authorization header")
    void testMe_NoAuthHeader() {
        // Act
        ResponseEntity<?> response = userController.me(null);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Missing or invalid Authorization header", response.getBody());
        verifyNoInteractions(jwtProvider);
    }

    @Test
    @DisplayName("Should return UNAUTHORIZED when Authorization header missing Bearer prefix")
    void testMe_MissingBearerPrefix() {
        // Arrange
        String authHeader = "InvalidToken123";

        // Act
        ResponseEntity<?> response = userController.me(authHeader);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Missing or invalid Authorization header", response.getBody());
        verifyNoInteractions(jwtProvider);
    }

    @Test
    @DisplayName("Should return UNAUTHORIZED when token is invalid")
    void testMe_InvalidToken() {
        // Arrange
        String token = "invalidToken";
        String authHeader = "Bearer " + token;
        when(jwtProvider.validateToken(token)).thenReturn(false);

        // Act
        ResponseEntity<?> response = userController.me(authHeader);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid token", response.getBody());
        verify(jwtProvider).validateToken(token);
        verify(jwtProvider, never()).getUsernameFromToken(anyString());
    }

    @Test
    @DisplayName("Should handle empty Authorization header")
    void testMe_EmptyAuthHeader() {
        // Arrange
        String authHeader = "";

        // Act
        ResponseEntity<?> response = userController.me(authHeader);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Missing or invalid Authorization header", response.getBody());
    }

    @Test
    @DisplayName("Should handle Authorization header with only Bearer")
    void testMe_BearerOnly() {
        // Arrange
        String authHeader = "Bearer ";

        // Act
        ResponseEntity<?> response = userController.me(authHeader);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        when(jwtProvider.validateToken("")).thenReturn(false);
    }

    @Test
    @DisplayName("Should extract username correctly from valid token")
    void testMe_CorrectUsernameExtraction() {
        // Arrange
        String token = "tokenABC";
        String authHeader = "Bearer " + token;
        String expectedUsername = "john.doe@example.com";

        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(jwtProvider.getUsernameFromToken(token)).thenReturn(expectedUsername);

        // Act
        ResponseEntity<?> response = userController.me(authHeader);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUsername, response.getBody());
    }

    @Test
    @DisplayName("Should handle token with special characters")
    void testMe_TokenWithSpecialCharacters() {
        // Arrange
        String token = "token.with-special_chars123";
        String authHeader = "Bearer " + token;

        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(jwtProvider.getUsernameFromToken(token)).thenReturn("user123");

        // Act
        ResponseEntity<?> response = userController.me(authHeader);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("user123", response.getBody());
    }

    @Test
    @DisplayName("Should handle long token")
    void testMe_LongToken() {
        // Arrange
        String token = "a".repeat(500);
        String authHeader = "Bearer " + token;

        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(jwtProvider.getUsernameFromToken(token)).thenReturn("testuser");

        // Act
        ResponseEntity<?> response = userController.me(authHeader);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testuser", response.getBody());
    }

    @Test
    @DisplayName("Should handle case-sensitive Bearer keyword")
    void testMe_LowercaseBearer() {
        // Arrange
        String authHeader = "bearer token123";

        // Act
        ResponseEntity<?> response = userController.me(authHeader);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Missing or invalid Authorization header", response.getBody());
    }

    @Test
    @DisplayName("Should validate token before extracting username")
    void testMe_ValidationOrder() {
        // Arrange
        String token = "token123";
        String authHeader = "Bearer " + token;
        when(jwtProvider.validateToken(token)).thenReturn(false);

        // Act
        ResponseEntity<?> response = userController.me(authHeader);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(jwtProvider).validateToken(token);
        verify(jwtProvider, never()).getUsernameFromToken(token);
    }

    @Test
    @DisplayName("Should handle whitespace in Bearer token")
    void testMe_WhitespaceInToken() {
        // Arrange
        String authHeader = "Bearer   token123";
        String token = "  token123";
        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(jwtProvider.getUsernameFromToken(token)).thenReturn("user");

        // Act
        ResponseEntity<?> response = userController.me(authHeader);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}


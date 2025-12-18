package dk.via.sep3.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PasswordService
 * Tests password hashing and matching functionality
 */
@ExtendWith(MockitoExtension.class)
class PasswordServiceTest {

    @Mock
    private BCryptPasswordEncoder encoder;

    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        passwordService = new PasswordService(encoder);
    }

    @Test
    @DisplayName("Should hash password using BCryptPasswordEncoder")
    void testHash_Success() {
        // Arrange
        String rawPassword = "MySecurePassword123";
        String hashedPassword = "$2a$10$someHashedPassword";
        when(encoder.encode(rawPassword)).thenReturn(hashedPassword);

        // Act
        String result = passwordService.hash(rawPassword);

        // Assert
        assertEquals(hashedPassword, result);
        verify(encoder, times(1)).encode(rawPassword);
    }

    @Test
    @DisplayName("Should return different hash for same password on multiple calls")
    void testHash_MultipleCallsDifferentHashes() {
        // Arrange
        String rawPassword = "MySecurePassword123";
        String hash1 = "$2a$10$hash1";
        String hash2 = "$2a$10$hash2";
        when(encoder.encode(rawPassword))
                .thenReturn(hash1)
                .thenReturn(hash2);

        // Act
        String result1 = passwordService.hash(rawPassword);
        String result2 = passwordService.hash(rawPassword);

        // Assert
        assertEquals(hash1, result1);
        assertEquals(hash2, result2);
        assertNotEquals(result1, result2);
        verify(encoder, times(2)).encode(rawPassword);
    }

    @Test
    @DisplayName("Should successfully match correct password with hash")
    void testMatches_CorrectPassword() {
        // Arrange
        String rawPassword = "MySecurePassword123";
        String hashedPassword = "$2a$10$someHashedPassword";
        when(encoder.matches(rawPassword, hashedPassword)).thenReturn(true);

        // Act
        boolean result = passwordService.matches(rawPassword, hashedPassword);

        // Assert
        assertTrue(result);
        verify(encoder, times(1)).matches(rawPassword, hashedPassword);
    }

    @Test
    @DisplayName("Should fail to match incorrect password with hash")
    void testMatches_IncorrectPassword() {
        // Arrange
        String rawPassword = "WrongPassword";
        String hashedPassword = "$2a$10$someHashedPassword";
        when(encoder.matches(rawPassword, hashedPassword)).thenReturn(false);

        // Act
        boolean result = passwordService.matches(rawPassword, hashedPassword);

        // Assert
        assertFalse(result);
        verify(encoder, times(1)).matches(rawPassword, hashedPassword);
    }

    @Test
    @DisplayName("Should handle null password during hashing")
    void testHash_NullPassword() {
        // Arrange
        when(encoder.encode(null)).thenThrow(new IllegalArgumentException("Password cannot be null"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> passwordService.hash(null));
        verify(encoder, times(1)).encode(null);
    }

    @Test
    @DisplayName("Should handle empty password during hashing")
    void testHash_EmptyPassword() {
        // Arrange
        String emptyPassword = "";
        String hashedEmpty = "$2a$10$hashedEmpty";
        when(encoder.encode(emptyPassword)).thenReturn(hashedEmpty);

        // Act
        String result = passwordService.hash(emptyPassword);

        // Assert
        assertEquals(hashedEmpty, result);
        verify(encoder, times(1)).encode(emptyPassword);
    }

    @Test
    @DisplayName("Should handle null values during password matching")
    void testMatches_NullValues() {
        // Arrange
        when(encoder.matches(null, null)).thenReturn(false);

        // Act
        boolean result = passwordService.matches(null, null);

        // Assert
        assertFalse(result);
        verify(encoder, times(1)).matches(null, null);
    }

    @Test
    @DisplayName("Should handle null raw password during matching")
    void testMatches_NullRawPassword() {
        // Arrange
        String hashedPassword = "$2a$10$someHashedPassword";
        when(encoder.matches(null, hashedPassword)).thenReturn(false);

        // Act
        boolean result = passwordService.matches(null, hashedPassword);

        // Assert
        assertFalse(result);
        verify(encoder, times(1)).matches(null, hashedPassword);
    }

    @Test
    @DisplayName("Should handle null hash during matching")
    void testMatches_NullHash() {
        // Arrange
        String rawPassword = "MySecurePassword123";
        when(encoder.matches(rawPassword, null)).thenReturn(false);

        // Act
        boolean result = passwordService.matches(rawPassword, null);

        // Assert
        assertFalse(result);
        verify(encoder, times(1)).matches(rawPassword, null);
    }

    @Test
    @DisplayName("Should delegate to encoder for special characters in password")
    void testHash_SpecialCharacters() {
        // Arrange
        String specialPassword = "P@ssw0rd!#$%^&*()";
        String hashedSpecial = "$2a$10$hashedSpecial";
        when(encoder.encode(specialPassword)).thenReturn(hashedSpecial);

        // Act
        String result = passwordService.hash(specialPassword);

        // Assert
        assertEquals(hashedSpecial, result);
        verify(encoder, times(1)).encode(specialPassword);
    }

    @Test
    @DisplayName("Should delegate to encoder for very long password")
    void testHash_LongPassword() {
        // Arrange
        String longPassword = "A".repeat(100);
        String hashedLong = "$2a$10$hashedLong";
        when(encoder.encode(longPassword)).thenReturn(hashedLong);

        // Act
        String result = passwordService.hash(longPassword);

        // Assert
        assertEquals(hashedLong, result);
        verify(encoder, times(1)).encode(longPassword);
    }

    @Test
    @DisplayName("Should correctly match password with special characters")
    void testMatches_SpecialCharacters() {
        // Arrange
        String specialPassword = "P@ssw0rd!#$%";
        String hashedSpecial = "$2a$10$hashedSpecial";
        when(encoder.matches(specialPassword, hashedSpecial)).thenReturn(true);

        // Act
        boolean result = passwordService.matches(specialPassword, hashedSpecial);

        // Assert
        assertTrue(result);
        verify(encoder, times(1)).matches(specialPassword, hashedSpecial);
    }
}


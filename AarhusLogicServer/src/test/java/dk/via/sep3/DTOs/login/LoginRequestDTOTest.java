package dk.via.sep3.DTOs.login;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LoginRequestDTO
 * Tests DTO construction and field access
 */
class LoginRequestDTOTest {

    @Test
    @DisplayName("Should create LoginRequestDTO with constructor")
    void testConstructor() {
        // Act
        LoginRequestDTO dto = new LoginRequestDTO("johndoe", "password123");

        // Assert
        assertNotNull(dto);
        assertEquals("johndoe", dto.getUsername());
        assertEquals("password123", dto.getPassword());
    }

    @Test
    @DisplayName("Should set and get username")
    void testSetGetUsername() {
        // Arrange
        LoginRequestDTO dto = new LoginRequestDTO("user1", "pass1");

        // Act
        dto.setUsername("user2");

        // Assert
        assertEquals("user2", dto.getUsername());
    }

    @Test
    @DisplayName("Should set and get password")
    void testSetGetPassword() {
        // Arrange
        LoginRequestDTO dto = new LoginRequestDTO("user", "pass1");

        // Act
        dto.setPassword("pass2");

        // Assert
        assertEquals("pass2", dto.getPassword());
    }

    @Test
    @DisplayName("Should handle null values")
    void testNullValues() {
        // Act
        LoginRequestDTO dto = new LoginRequestDTO(null, null);

        // Assert
        assertNull(dto.getUsername());
        assertNull(dto.getPassword());
    }

    @Test
    @DisplayName("Should handle empty strings")
    void testEmptyStrings() {
        // Act
        LoginRequestDTO dto = new LoginRequestDTO("", "");

        // Assert
        assertEquals("", dto.getUsername());
        assertEquals("", dto.getPassword());
    }

    @Test
    @DisplayName("Should handle special characters in username")
    void testSpecialCharactersInUsername() {
        // Act
        LoginRequestDTO dto = new LoginRequestDTO("user@example.com", "pass");

        // Assert
        assertEquals("user@example.com", dto.getUsername());
    }

    @Test
    @DisplayName("Should handle special characters in password")
    void testSpecialCharactersInPassword() {
        // Act
        LoginRequestDTO dto = new LoginRequestDTO("user", "P@ssw0rd!#$%");

        // Assert
        assertEquals("P@ssw0rd!#$%", dto.getPassword());
    }

    @Test
    @DisplayName("Should preserve values after creation")
    void testPreserveValues() {
        // Arrange
        String username = "testuser";
        String password = "testpass";

        // Act
        LoginRequestDTO dto = new LoginRequestDTO(username, password);

        // Assert
        assertEquals(username, dto.getUsername());
        assertEquals(password, dto.getPassword());
    }

    @Test
    @DisplayName("Should allow field updates")
    void testFieldUpdates() {
        // Arrange
        LoginRequestDTO dto = new LoginRequestDTO("user1", "pass1");

        // Act
        dto.setUsername("user2");
        dto.setPassword("pass2");

        // Assert
        assertEquals("user2", dto.getUsername());
        assertEquals("pass2", dto.getPassword());
    }

    @Test
    @DisplayName("Should handle very long password")
    void testVeryLongPassword() {
        // Arrange
        String longPassword = "a".repeat(500);

        // Act
        LoginRequestDTO dto = new LoginRequestDTO("user", longPassword);

        // Assert
        assertEquals(longPassword, dto.getPassword());
    }
}


package dk.via.sep3.DTOs.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserDTO
 * Tests DTO construction and field access
 */
class UserDTOTest {

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
    }

    @Test
    @DisplayName("Should create UserDTO with no-arg constructor")
    void testNoArgConstructor() {
        // Act
        UserDTO dto = new UserDTO();

        // Assert
        assertNotNull(dto);
        assertNull(dto.getUsername());
        assertNull(dto.getName());
        assertNull(dto.getEmail());
        assertNull(dto.getPhoneNumber());
        assertNull(dto.getRole());
    }

    @Test
    @DisplayName("Should set and get username")
    void testSetGetUsername() {
        // Act
        userDTO.setUsername("johndoe");

        // Assert
        assertEquals("johndoe", userDTO.getUsername());
    }

    @Test
    @DisplayName("Should set and get name")
    void testSetGetName() {
        // Act
        userDTO.setFullName("John Doe");

        // Assert
        assertEquals("John Doe", userDTO.getName());
    }

    @Test
    @DisplayName("Should set and get email")
    void testSetGetEmail() {
        // Act
        userDTO.setEmail("john@example.com");

        // Assert
        assertEquals("john@example.com", userDTO.getEmail());
    }

    @Test
    @DisplayName("Should set and get phone number")
    void testSetGetPhoneNumber() {
        // Act
        userDTO.setPhoneNumber("12345678");

        // Assert
        assertEquals("12345678", userDTO.getPhoneNumber());
    }

    @Test
    @DisplayName("Should set and get role")
    void testSetGetRole() {
        // Act
        userDTO.setRole("Reader");

        // Assert
        assertEquals("Reader", userDTO.getRole());
    }

    @Test
    @DisplayName("Should handle null values")
    void testNullValues() {
        // Act
        userDTO.setUsername(null);
        userDTO.setFullName(null);
        userDTO.setEmail(null);
        userDTO.setPhoneNumber(null);
        userDTO.setRole(null);

        // Assert
        assertNull(userDTO.getUsername());
        assertNull(userDTO.getName());
        assertNull(userDTO.getEmail());
        assertNull(userDTO.getPhoneNumber());
        assertNull(userDTO.getRole());
    }

    @Test
    @DisplayName("Should handle empty strings")
    void testEmptyStrings() {
        // Act
        userDTO.setUsername("");
        userDTO.setFullName("");
        userDTO.setEmail("");
        userDTO.setPhoneNumber("");
        userDTO.setRole("");

        // Assert
        assertEquals("", userDTO.getUsername());
        assertEquals("", userDTO.getName());
        assertEquals("", userDTO.getEmail());
        assertEquals("", userDTO.getPhoneNumber());
        assertEquals("", userDTO.getRole());
    }

    @Test
    @DisplayName("Should handle different roles")
    void testDifferentRoles() {
        // Reader
        userDTO.setRole("Reader");
        assertEquals("Reader", userDTO.getRole());

        // Librarian
        userDTO.setRole("Librarian");
        assertEquals("Librarian", userDTO.getRole());

        // Admin
        userDTO.setRole("Admin");
        assertEquals("Admin", userDTO.getRole());
    }

    @Test
    @DisplayName("Should handle special characters in username")
    void testSpecialCharactersInUsername() {
        // Act
        userDTO.setUsername("user@example.com");

        // Assert
        assertEquals("user@example.com", userDTO.getUsername());
    }

    @Test
    @DisplayName("Should handle special characters in name")
    void testSpecialCharactersInName() {
        // Act
        userDTO.setFullName("José María García-López");

        // Assert
        assertEquals("José María García-López", userDTO.getName());
    }

    @Test
    @DisplayName("Should handle different email formats")
    void testDifferentEmailFormats() {
        // Standard
        userDTO.setEmail("user@example.com");
        assertEquals("user@example.com", userDTO.getEmail());

        // With plus
        userDTO.setEmail("user+test@example.com");
        assertEquals("user+test@example.com", userDTO.getEmail());

        // Subdomain
        userDTO.setEmail("user@mail.example.com");
        assertEquals("user@mail.example.com", userDTO.getEmail());
    }

    @Test
    @DisplayName("Should handle different phone formats")
    void testDifferentPhoneFormats() {
        // Digits only
        userDTO.setPhoneNumber("12345678");
        assertEquals("12345678", userDTO.getPhoneNumber());

        // With spaces
        userDTO.setPhoneNumber("12 34 56 78");
        assertEquals("12 34 56 78", userDTO.getPhoneNumber());

        // With country code
        userDTO.setPhoneNumber("+45 12 34 56 78");
        assertEquals("+45 12 34 56 78", userDTO.getPhoneNumber());
    }

    @Test
    @DisplayName("Should preserve all fields")
    void testPreserveAllFields() {
        // Act
        userDTO.setUsername("testuser");
        userDTO.setFullName("Test User");
        userDTO.setEmail("test@example.com");
        userDTO.setPhoneNumber("99887766");
        userDTO.setRole("Reader");

        // Assert
        assertEquals("testuser", userDTO.getUsername());
        assertEquals("Test User", userDTO.getName());
        assertEquals("test@example.com", userDTO.getEmail());
        assertEquals("99887766", userDTO.getPhoneNumber());
        assertEquals("Reader", userDTO.getRole());
    }

    @Test
    @DisplayName("Should handle very long strings")
    void testVeryLongStrings() {
        // Arrange
        String longString = "a".repeat(1000);

        // Act
        userDTO.setUsername(longString);
        userDTO.setFullName(longString);

        // Assert
        assertEquals(longString, userDTO.getUsername());
        assertEquals(longString, userDTO.getName());
    }

    @Test
    @DisplayName("Should allow field updates")
    void testFieldUpdates() {
        // Arrange
        userDTO.setUsername("original");
        userDTO.setRole("Reader");

        // Act
        userDTO.setUsername("updated");
        userDTO.setRole("Librarian");

        // Assert
        assertEquals("updated", userDTO.getUsername());
        assertEquals("Librarian", userDTO.getRole());
    }
}


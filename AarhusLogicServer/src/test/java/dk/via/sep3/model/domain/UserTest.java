package dk.via.sep3.model.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for User domain class
 * Tests domain object construction and field access
 */
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    @DisplayName("Should create User with no-arg constructor")
    void testNoArgConstructor() {
        // Act
        User u = new User();

        // Assert
        assertNotNull(u);
        assertNull(u.getUsername());
        assertNull(u.getName());
        assertNull(u.getPassword());
        assertNull(u.getRole());
        assertNull(u.getEmail());
        assertNull(u.getPhoneNumber());
    }

    @Test
    @DisplayName("Should create User with all args constructor")
    void testAllArgsConstructor() {
        // Act
        User u = new User(
            "John Doe",
            "johndoe",
            "hashedPassword123",
            "Reader",
            "12345678",
            "john@example.com"
        );

        // Assert
        assertEquals("johndoe", u.getUsername());
        assertEquals("John Doe", u.getName());
        assertEquals("hashedPassword123", u.getPassword());
        assertEquals("Reader", u.getRole());
        assertEquals("john@example.com", u.getEmail());
        assertEquals("12345678", u.getPhoneNumber());
    }

    @Test
    @DisplayName("Should set and get username")
    void testSetGetUsername() {
        // Act
        user.setUsername("testuser");

        // Assert
        assertEquals("testuser", user.getUsername());
    }

    @Test
    @DisplayName("Should set and get name")
    void testSetGetName() {
        // Act
        user.setName("Test User");

        // Assert
        assertEquals("Test User", user.getName());
    }

    @Test
    @DisplayName("Should set and get password hash")
    void testSetGetPasswordHash() {
        // Act
        user.setPassword("$2a$10$hashedPassword");

        // Assert
        assertEquals("$2a$10$hashedPassword", user.getPassword());
    }

    @Test
    @DisplayName("Should set and get role")
    void testSetGetRole() {
        // Act
        user.setRole("Librarian");

        // Assert
        assertEquals("Librarian", user.getRole());
    }

    @Test
    @DisplayName("Should set and get email")
    void testSetGetEmail() {
        // Act
        user.setEmail("user@example.com");

        // Assert
        assertEquals("user@example.com", user.getEmail());
    }

    @Test
    @DisplayName("Should set and get phone number")
    void testSetGetPhoneNumber() {
        // Act
        user.setPhoneNumber("99887766");

        // Assert
        assertEquals("99887766", user.getPhoneNumber());
    }

    @Test
    @DisplayName("Should handle null values")
    void testNullValues() {
        // Act
        user.setUsername(null);
        user.setName(null);
        user.setPassword(null);
        user.setRole(null);
        user.setEmail(null);
        user.setPhoneNumber(null);

        // Assert
        assertNull(user.getUsername());
        assertNull(user.getName());
        assertNull(user.getPassword());
        assertNull(user.getRole());
        assertNull(user.getEmail());
        assertNull(user.getPhoneNumber());
    }

    @Test
    @DisplayName("Should handle empty strings")
    void testEmptyStrings() {
        // Act
        user.setUsername("");
        user.setName("");
        user.setPassword("");
        user.setRole("");
        user.setEmail("");
        user.setPhoneNumber("");

        // Assert
        assertEquals("", user.getUsername());
        assertEquals("", user.getName());
        assertEquals("", user.getPassword());
        assertEquals("", user.getRole());
        assertEquals("", user.getEmail());
        assertEquals("", user.getPhoneNumber());
    }

    @Test
    @DisplayName("Should handle different roles")
    void testDifferentRoles() {
        // Reader
        user.setRole("Reader");
        assertEquals("Reader", user.getRole());

        // Librarian
        user.setRole("Librarian");
        assertEquals("Librarian", user.getRole());

        // Admin
        user.setRole("Admin");
        assertEquals("Admin", user.getRole());
    }

    @Test
    @DisplayName("Should preserve all fields in constructor")
    void testConstructor_PreservesAllFields() {
        // Act
        User u = new User(
            "User Name",
            "user123",
            "$2a$10$hash",
            "Reader",
            "11223344",
            "user@test.com"
        );

        // Assert
        assertEquals("user123", u.getUsername());
        assertEquals("User Name", u.getName());
        assertEquals("$2a$10$hash", u.getPassword());
        assertEquals("Reader", u.getRole());
        assertEquals("user@test.com", u.getEmail());
        assertEquals("11223344", u.getPhoneNumber());
    }

    @Test
    @DisplayName("Should handle BCrypt password hash")
    void testBCryptPasswordHash() {
        // Act
        user.setPassword("$2a$10$abcdefghijklmnopqrstuvwxyz1234567890");

        // Assert
        assertTrue(user.getPassword().startsWith("$2a$10$"));
    }

    @Test
    @DisplayName("Should handle special characters in name")
    void testSpecialCharactersInName() {
        // Act
        user.setName("José María García-López");

        // Assert
        assertEquals("José María García-López", user.getName());
    }

    @Test
    @DisplayName("Should handle special characters in username")
    void testSpecialCharactersInUsername() {
        // Act
        user.setUsername("user.name_123");

        // Assert
        assertEquals("user.name_123", user.getUsername());
    }

    @Test
    @DisplayName("Should handle different email formats")
    void testDifferentEmailFormats() {
        // Standard
        user.setEmail("user@example.com");
        assertEquals("user@example.com", user.getEmail());

        // With subdomain
        user.setEmail("user@mail.example.com");
        assertEquals("user@mail.example.com", user.getEmail());

        // With plus
        user.setEmail("user+test@example.com");
        assertEquals("user+test@example.com", user.getEmail());
    }

    @Test
    @DisplayName("Should handle different phone formats")
    void testDifferentPhoneFormats() {
        // Digits only
        user.setPhoneNumber("12345678");
        assertEquals("12345678", user.getPhoneNumber());

        // With country code
        user.setPhoneNumber("+4512345678");
        assertEquals("+4512345678", user.getPhoneNumber());

        // With spaces
        user.setPhoneNumber("12 34 56 78");
        assertEquals("12 34 56 78", user.getPhoneNumber());
    }

    @Test
    @DisplayName("Should allow field updates")
    void testFieldUpdates() {
        // Arrange
        user.setUsername("original");
        user.setRole("Reader");

        // Act
        user.setUsername("updated");
        user.setRole("Librarian");

        // Assert
        assertEquals("updated", user.getUsername());
        assertEquals("Librarian", user.getRole());
    }

    @Test
    @DisplayName("Should handle very long strings")
    void testVeryLongStrings() {
        // Arrange
        String longString = "a".repeat(500);

        // Act
        user.setName(longString);
        user.setPassword(longString);

        // Assert
        assertEquals(longString, user.getName());
        assertEquals(longString, user.getPassword());
    }
}


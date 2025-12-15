package dk.via.sep3.shared.registration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RegistrationDTO
 * Tests DTO construction and field access
 */
class RegistrationDTOTest {

    private RegistrationDTO dto;

    @BeforeEach
    void setUp() {
        dto = new RegistrationDTO();
    }

    @Test
    @DisplayName("Should create RegistrationDTO with no-arg constructor")
    void testNoArgConstructor() {
        // Act
        RegistrationDTO regDto = new RegistrationDTO();

        // Assert
        assertNotNull(regDto);
        assertNull(regDto.getFullName());
        assertNull(regDto.getEmail());
        assertNull(regDto.getPhone());
        assertNull(regDto.getUsername());
        assertNull(regDto.getPassword());
    }

    @Test
    @DisplayName("Should set and get full name")
    void testSetGetFullName() {
        // Act
        dto.setFullName("John Doe");

        // Assert
        assertEquals("John Doe", dto.getFullName());
    }

    @Test
    @DisplayName("Should set and get email")
    void testSetGetEmail() {
        // Act
        dto.setEmail("john@example.com");

        // Assert
        assertEquals("john@example.com", dto.getEmail());
    }

    @Test
    @DisplayName("Should set and get phone")
    void testSetGetPhone() {
        // Act
        dto.setPhone("12345678");

        // Assert
        assertEquals("12345678", dto.getPhone());
    }

    @Test
    @DisplayName("Should set and get username")
    void testSetGetUsername() {
        // Act
        dto.setUsername("johndoe");

        // Assert
        assertEquals("johndoe", dto.getUsername());
    }

    @Test
    @DisplayName("Should set and get password")
    void testSetGetPassword() {
        // Act
        dto.setPassword("SecureP@ss123");

        // Assert
        assertEquals("SecureP@ss123", dto.getPassword());
    }

    @Test
    @DisplayName("Should handle null values")
    void testNullValues() {
        // Act
        dto.setFullName(null);
        dto.setEmail(null);
        dto.setPhone(null);
        dto.setUsername(null);
        dto.setPassword(null);

        // Assert
        assertNull(dto.getFullName());
        assertNull(dto.getEmail());
        assertNull(dto.getPhone());
        assertNull(dto.getUsername());
        assertNull(dto.getPassword());
    }

    @Test
    @DisplayName("Should handle empty strings")
    void testEmptyStrings() {
        // Act
        dto.setFullName("");
        dto.setEmail("");
        dto.setPhone("");
        dto.setUsername("");
        dto.setPassword("");

        // Assert
        assertEquals("", dto.getFullName());
        assertEquals("", dto.getEmail());
        assertEquals("", dto.getPhone());
        assertEquals("", dto.getUsername());
        assertEquals("", dto.getPassword());
    }

    @Test
    @DisplayName("Should handle special characters in name")
    void testSpecialCharactersInName() {
        // Act
        dto.setFullName("José María García-López");

        // Assert
        assertEquals("José María García-López", dto.getFullName());
    }

    @Test
    @DisplayName("Should handle different email formats")
    void testDifferentEmailFormats() {
        // Standard
        dto.setEmail("user@example.com");
        assertEquals("user@example.com", dto.getEmail());

        // With plus
        dto.setEmail("user+test@example.com");
        assertEquals("user+test@example.com", dto.getEmail());

        // Subdomain
        dto.setEmail("user@mail.example.com");
        assertEquals("user@mail.example.com", dto.getEmail());
    }

    @Test
    @DisplayName("Should handle different phone formats")
    void testDifferentPhoneFormats() {
        // Digits only
        dto.setPhone("12345678");
        assertEquals("12345678", dto.getPhone());

        // With country code
        dto.setPhone("+4512345678");
        assertEquals("+4512345678", dto.getPhone());

        // With spaces
        dto.setPhone("12 34 56 78");
        assertEquals("12 34 56 78", dto.getPhone());
    }

    @Test
    @DisplayName("Should preserve all fields")
    void testPreserveAllFields() {
        // Act
        dto.setFullName("Test User");
        dto.setEmail("test@example.com");
        dto.setPhone("99887766");
        dto.setUsername("testuser");
        dto.setPassword("TestPass123!");

        // Assert
        assertEquals("Test User", dto.getFullName());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals("99887766", dto.getPhone());
        assertEquals("testuser", dto.getUsername());
        assertEquals("TestPass123!", dto.getPassword());
    }

    @Test
    @DisplayName("Should allow field updates")
    void testFieldUpdates() {
        // Arrange
        dto.setFullName("Original");
        dto.setUsername("original");

        // Act
        dto.setFullName("Updated");
        dto.setUsername("updated");

        // Assert
        assertEquals("Updated", dto.getFullName());
        assertEquals("updated", dto.getUsername());
    }

    @Test
    @DisplayName("Should handle complex password")
    void testComplexPassword() {
        // Act
        dto.setPassword("P@ssw0rd!#$%^&*()");

        // Assert
        assertEquals("P@ssw0rd!#$%^&*()", dto.getPassword());
    }

    @Test
    @DisplayName("Should handle very long strings")
    void testVeryLongStrings() {
        // Arrange
        String longString = "a".repeat(500);

        // Act
        dto.setFullName(longString);
        dto.setPassword(longString);

        // Assert
        assertEquals(longString, dto.getFullName());
        assertEquals(longString, dto.getPassword());
    }
}


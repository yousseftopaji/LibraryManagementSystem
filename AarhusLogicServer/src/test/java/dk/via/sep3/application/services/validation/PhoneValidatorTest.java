package dk.via.sep3.application.services.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PhoneValidator
 * Tests phone number validation logic
 */
class PhoneValidatorTest {

    private PhoneValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PhoneValidator();
    }

    @Test
    @DisplayName("Should accept valid 8 digit phone number")
    void testValidPhone_8Digits() {
        // Act & Assert
        assertDoesNotThrow(() -> validator.validate("12345678"));
    }

    @Test
    @DisplayName("Should accept valid 10 digit phone number")
    void testValidPhone_10Digits() {
        // Act & Assert
        assertDoesNotThrow(() -> validator.validate("1234567890"));
    }

    @Test
    @DisplayName("Should accept very long phone number")
    void testValidPhone_VeryLong() {
        // Arrange
        String longPhone = "1".repeat(20);

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(longPhone));
    }


    @Test
    @DisplayName("Should accept phone with exactly 8 digits")
    void testBoundaryCondition_Exactly8Digits() {
        // Act & Assert
        assertDoesNotThrow(() -> validator.validate("88888888"));
    }

    @Test
    @DisplayName("Should accept phone with spaces but enough digits")
    void testValidPhone_WithSpaces() {
        // Act & Assert (10 digits with spaces)
        assertDoesNotThrow(() -> validator.validate("12 34 56 78 90"));
    }

    @Test
    @DisplayName("Should accept phone with country code")
    void testValidPhone_WithCountryCode() {
        // Act & Assert
        assertDoesNotThrow(() -> validator.validate("+4512345678"));
    }

    @Test
    @DisplayName("Should accept phone with dashes")
    void testValidPhone_WithDashes() {
        // Act & Assert
        assertDoesNotThrow(() -> validator.validate("12-34-56-78"));
    }
}


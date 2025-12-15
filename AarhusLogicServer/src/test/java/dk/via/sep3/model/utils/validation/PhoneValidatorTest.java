package dk.via.sep3.model.utils.validation;

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
    @DisplayName("Should throw exception for phone with less than 8 digits")
    void testInvalidPhone_LessThan8Digits() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validate("1234567")
        );
        assertEquals("Phone number must contain at least 8 digits", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for 7 digit phone")
    void testInvalidPhone_7Digits() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validate("7654321")
        );
        assertEquals("Phone number must contain at least 8 digits", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for empty phone")
    void testInvalidPhone_Empty() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validate("")
        );
        assertEquals("Phone number must contain at least 8 digits", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for null phone")
    void testInvalidPhone_Null() {
        // Act & Assert
        assertThrows(
            NullPointerException.class,
            () -> validator.validate(null)
        );
    }

    @Test
    @DisplayName("Should accept phone with exactly 8 digits")
    void testBoundaryCondition_Exactly8Digits() {
        // Act & Assert
        assertDoesNotThrow(() -> validator.validate("88888888"));
    }

    @Test
    @DisplayName("Should throw exception for letters only")
    void testInvalidPhone_LettersOnly() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validate("abcdefgh")
        );
        assertEquals("Phone number must contain at least 8 digits", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for special characters only")
    void testInvalidPhone_SpecialCharactersOnly() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validate("!@#$%^&*")
        );
        assertEquals("Phone number must contain at least 8 digits", exception.getMessage());
    }

    @Test
    @DisplayName("Should accept phone with spaces but enough digits")
    void testValidPhone_WithSpaces() {
        // Act & Assert (10 digits with spaces)
        assertDoesNotThrow(() -> validator.validate("12 34 56 78 90"));
    }

    @Test
    @DisplayName("Should throw exception for phone with spaces but not enough digits")
    void testInvalidPhone_WithSpaces() {
        // Act & Assert (only 7 digits)
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validate("12 34 56 7")
        );
        assertEquals("Phone number must contain at least 8 digits", exception.getMessage());
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

    @Test
    @DisplayName("Should throw exception for single digit")
    void testInvalidPhone_SingleDigit() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validate("1")
        );
        assertEquals("Phone number must contain at least 8 digits", exception.getMessage());
    }
}


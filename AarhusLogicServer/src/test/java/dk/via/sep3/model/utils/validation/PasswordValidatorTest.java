package dk.via.sep3.model.utils.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PasswordValidator
 * Tests password validation logic
 */
class PasswordValidatorTest {

    private PasswordValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PasswordValidator();
    }

    @Test
    @DisplayName("Should accept valid password with 8 characters")
    void testValidPassword_8Characters() {
        // Act & Assert
        assertDoesNotThrow(() -> validator.validate("Pass1234"));
    }

    @Test
    @DisplayName("Should accept valid password with more than 8 characters")
    void testValidPassword_MoreThan8Characters() {
        // Act & Assert
        assertDoesNotThrow(() -> validator.validate("Password123"));
    }

    @Test
    @DisplayName("Should accept password with special characters")
    void testValidPassword_WithSpecialCharacters() {
        // Act & Assert
        assertDoesNotThrow(() -> validator.validate("P@ssw0rd!"));
    }

    @Test
    @DisplayName("Should accept very long password")
    void testValidPassword_VeryLong() {
        // Arrange
        String longPassword = "A".repeat(100);

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate(longPassword));
    }

    @Test
    @DisplayName("Should throw exception for password less than 8 characters")
    void testInvalidPassword_LessThan8Characters() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validate("Pass123")
        );
        assertEquals("Password must be at least 8 characters long", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for 7 character password")
    void testInvalidPassword_7Characters() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validate("1234567")
        );
        assertEquals("Password must be at least 8 characters long", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for empty password")
    void testInvalidPassword_Empty() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validate("")
        );
        assertEquals("Password must be at least 8 characters long", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception for null password")
    void testInvalidPassword_Null() {
        // Act & Assert
        assertThrows(
            NullPointerException.class,
            () -> validator.validate(null)
        );
    }

    @Test
    @DisplayName("Should accept password with exactly 8 characters")
    void testBoundaryCondition_Exactly8Characters() {
        // Act & Assert
        assertDoesNotThrow(() -> validator.validate("12345678"));
    }

    @Test
    @DisplayName("Should accept password with numbers only")
    void testValidPassword_NumbersOnly() {
        // Act & Assert
        assertDoesNotThrow(() -> validator.validate("12345678"));
    }

    @Test
    @DisplayName("Should accept password with letters only")
    void testValidPassword_LettersOnly() {
        // Act & Assert
        assertDoesNotThrow(() -> validator.validate("abcdefgh"));
    }

    @Test
    @DisplayName("Should accept password with mixed case")
    void testValidPassword_MixedCase() {
        // Act & Assert
        assertDoesNotThrow(() -> validator.validate("AbCdEfGh"));
    }

    @Test
    @DisplayName("Should accept password with spaces")
    void testValidPassword_WithSpaces() {
        // Act & Assert
        assertDoesNotThrow(() -> validator.validate("Pass word"));
    }

    @Test
    @DisplayName("Should accept password with unicode characters")
    void testValidPassword_UnicodeCharacters() {
        // Act & Assert
        assertDoesNotThrow(() -> validator.validate("Pässwörd"));
    }

    @Test
    @DisplayName("Should throw exception for single character")
    void testInvalidPassword_SingleCharacter() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> validator.validate("a")
        );
        assertEquals("Password must be at least 8 characters long", exception.getMessage());
    }
}


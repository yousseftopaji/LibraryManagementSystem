package dk.via.sep3.application.services.validation;

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
}


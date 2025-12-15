package dk.via.sep3.model.utils.validation;

import dk.via.sep3.model.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LoginValidatorImpl
 * Tests login validation logic with mocked dependencies
 */
@ExtendWith(MockitoExtension.class)
class LoginValidatorImplTest {

    @Mock
    private Validator<String> userValidator;

    private LoginValidatorImpl loginValidator;

    @BeforeEach
    void setUp() {
        loginValidator = new LoginValidatorImpl(userValidator);
    }

    @Test
    @DisplayName("Should create LoginValidatorImpl with dependencies")
    void testConstructor() {
        // Act
        LoginValidatorImpl validator = new LoginValidatorImpl(userValidator);

        // Assert
        assertNotNull(validator);
    }

    @Test
    @DisplayName("Should call validate with valid user")
    void testValidateWithValidUser() {
        // Arrange
        User user = new User("John Doe", "johndoe", "password123",
            "Reader", "12345678", "john@example.com");

        // Act
        assertDoesNotThrow(() -> loginValidator.validate(user));

        // Assert - method completes without error
    }

    @Test
    @DisplayName("Should handle user with all fields")
    void testValidateWithAllFields() {
        // Arrange
        User user = new User("Jane Doe", "janedoe", "pass123",
            "Reader", "98765432", "jane@example.com");

        // Act
        assertDoesNotThrow(() -> loginValidator.validate(user));
    }

    @Test
    @DisplayName("Should handle user with null fields")
    void testValidateWithNullFields() {
        // Arrange
        User user = new User(null, null, null, null, null, null);

        // Act
        assertDoesNotThrow(() -> loginValidator.validate(user));
    }

    @Test
    @DisplayName("Should handle null user")
    void testValidateWithNullUser() {
        // Act
        assertDoesNotThrow(() -> loginValidator.validate(null));
    }

    @Test
    @DisplayName("Should handle user with empty strings")
    void testValidateWithEmptyStrings() {
        // Arrange
        User user = new User("", "", "", "", "", "");

        // Act
        assertDoesNotThrow(() -> loginValidator.validate(user));
    }

    @Test
    @DisplayName("Should handle user with special characters")
    void testValidateWithSpecialCharacters() {
        // Arrange
        User user = new User("José María", "user@example.com", "P@ssw0rd!",
            "Reader", "+4512345678", "user@test.com");

        // Act
        assertDoesNotThrow(() -> loginValidator.validate(user));
    }

    @Test
    @DisplayName("Should handle user with very long fields")
    void testValidateWithVeryLongFields() {
        // Arrange
        String longString = "a".repeat(500);
        User user = new User(longString, longString, longString,
            "Reader", longString, longString);

        // Act
        assertDoesNotThrow(() -> loginValidator.validate(user));
    }

    @Test
    @DisplayName("Should handle user with minimum fields")
    void testValidateWithMinimumFields() {
        // Arrange
        User user = new User();

        // Act
        assertDoesNotThrow(() -> loginValidator.validate(user));
    }

    @Test
    @DisplayName("Should handle different roles")
    void testValidateWithDifferentRoles() {
        // Reader
        User reader = new User("User", "user1", "pass", "Reader", "12345678", "user@test.com");
        assertDoesNotThrow(() -> loginValidator.validate(reader));

        // Librarian
        User librarian = new User("User", "user2", "pass", "Librarian", "12345678", "user@test.com");
        assertDoesNotThrow(() -> loginValidator.validate(librarian));

        // Admin
        User admin = new User("User", "user3", "pass", "Admin", "12345678", "user@test.com");
        assertDoesNotThrow(() -> loginValidator.validate(admin));
    }
}


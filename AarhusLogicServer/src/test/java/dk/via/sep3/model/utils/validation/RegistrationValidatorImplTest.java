package dk.via.sep3.model.utils.validation;

import dk.via.sep3.model.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

/**
 * Unit tests for RegistrationValidatorImpl
 * Tests validation logic for user registration
 */
@ExtendWith(MockitoExtension.class)
class RegistrationValidatorImplTest {

    @Mock
    private Validator<String> emailValidator;

    @Mock
    private Validator<String> phoneValidator;

    @Mock
    private Validator<String> passwordValidator;

    @Mock
    private Validator<String> usernameValidator;

    private RegistrationValidatorImpl registrationValidator;

    private User validUser;

    @BeforeEach
    void setUp() {
        registrationValidator = new RegistrationValidatorImpl(
                emailValidator,
                phoneValidator,
                passwordValidator,
                usernameValidator
        );

        validUser = new User();
        validUser.setName("John Doe");
        validUser.setEmail("john.doe@example.com");
        validUser.setPhoneNumber("12345678");
        validUser.setUsername("johndoe");
        validUser.setPassword("SecurePass123");
    }

    @Test
    @DisplayName("Should validate all fields when user is valid")
    void testValidate_ValidUser() {
        // Arrange
        doNothing().when(emailValidator).validate(anyString());
        doNothing().when(phoneValidator).validate(anyString());
        doNothing().when(passwordValidator).validate(anyString());
        doNothing().when(usernameValidator).validate(anyString());

        // Act
        registrationValidator.validate(validUser);

        // Assert
        verify(emailValidator, times(1)).validate("john.doe@example.com");
        verify(phoneValidator, times(1)).validate("12345678");
        verify(passwordValidator, times(1)).validate("SecurePass123");
        verify(usernameValidator, times(1)).validate("johndoe");
    }

    @Test
    @DisplayName("Should call validators in correct order")
    void testValidate_ValidatorsCalledInOrder() {
        // Arrange
        doNothing().when(emailValidator).validate(anyString());
        doNothing().when(phoneValidator).validate(anyString());
        doNothing().when(passwordValidator).validate(anyString());
        doNothing().when(usernameValidator).validate(anyString());

        // Act
        registrationValidator.validate(validUser);

        // Assert - verify order using InOrder
        var inOrder = inOrder(emailValidator, phoneValidator, passwordValidator, usernameValidator);
        inOrder.verify(emailValidator).validate("john.doe@example.com");
        inOrder.verify(phoneValidator).validate("12345678");
        inOrder.verify(passwordValidator).validate("SecurePass123");
        inOrder.verify(usernameValidator).validate("johndoe");
    }

    @Test
    @DisplayName("Should stop at first validation failure")
    void testValidate_EmailValidationFails() {
        // Arrange
        doThrow(new IllegalArgumentException("Invalid email format"))
                .when(emailValidator).validate(anyString());

        // Act & Assert
        try {
            registrationValidator.validate(validUser);
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Verify email validator was called but others were not
        verify(emailValidator, times(1)).validate("john.doe@example.com");
        verify(phoneValidator, never()).validate(anyString());
        verify(passwordValidator, never()).validate(anyString());
        verify(usernameValidator, never()).validate(anyString());
    }

    @Test
    @DisplayName("Should validate phone when email passes but phone fails")
    void testValidate_PhoneValidationFails() {
        // Arrange
        doNothing().when(emailValidator).validate(anyString());
        doThrow(new IllegalArgumentException("Invalid phone number"))
                .when(phoneValidator).validate(anyString());

        // Act & Assert
        try {
            registrationValidator.validate(validUser);
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Verify email and phone validators were called
        verify(emailValidator, times(1)).validate("john.doe@example.com");
        verify(phoneValidator, times(1)).validate("12345678");
        verify(passwordValidator, never()).validate(anyString());
        verify(usernameValidator, never()).validate(anyString());
    }

    @Test
    @DisplayName("Should validate password when email and phone pass but password fails")
    void testValidate_PasswordValidationFails() {
        // Arrange
        doNothing().when(emailValidator).validate(anyString());
        doNothing().when(phoneValidator).validate(anyString());
        doThrow(new IllegalArgumentException("Password too weak"))
                .when(passwordValidator).validate(anyString());

        // Act & Assert
        try {
            registrationValidator.validate(validUser);
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Verify email, phone, and password validators were called
        verify(emailValidator, times(1)).validate("john.doe@example.com");
        verify(phoneValidator, times(1)).validate("12345678");
        verify(passwordValidator, times(1)).validate("SecurePass123");
        verify(usernameValidator, never()).validate(anyString());
    }

    @Test
    @DisplayName("Should validate username when all others pass but username fails")
    void testValidate_UsernameValidationFails() {
        // Arrange
        doNothing().when(emailValidator).validate(anyString());
        doNothing().when(phoneValidator).validate(anyString());
        doNothing().when(passwordValidator).validate(anyString());
        doThrow(new IllegalArgumentException("Username already exists"))
                .when(usernameValidator).validate(anyString());

        // Act & Assert
        try {
            registrationValidator.validate(validUser);
        } catch (IllegalArgumentException e) {
            // Expected
        }

        // Verify all validators were called
        verify(emailValidator, times(1)).validate("john.doe@example.com");
        verify(phoneValidator, times(1)).validate("12345678");
        verify(passwordValidator, times(1)).validate("SecurePass123");
        verify(usernameValidator, times(1)).validate("johndoe");
    }

    @Test
    @DisplayName("Should handle null values in user fields")
    void testValidate_NullFields() {
        // Arrange
        User userWithNulls = new User();
        userWithNulls.setEmail(null);
        userWithNulls.setPhoneNumber(null);
        userWithNulls.setPassword(null);
        userWithNulls.setUsername(null);

        doNothing().when(emailValidator).validate(null);
        doNothing().when(phoneValidator).validate(null);
        doNothing().when(passwordValidator).validate(null);
        doNothing().when(usernameValidator).validate(null);

        // Act
        registrationValidator.validate(userWithNulls);

        // Assert
        verify(emailValidator, times(1)).validate(null);
        verify(phoneValidator, times(1)).validate(null);
        verify(passwordValidator, times(1)).validate(null);
        verify(usernameValidator, times(1)).validate(null);
    }

    @Test
    @DisplayName("Should handle empty string values")
    void testValidate_EmptyStrings() {
        // Arrange
        User userWithEmptyStrings = new User();
        userWithEmptyStrings.setEmail("");
        userWithEmptyStrings.setPhoneNumber("");
        userWithEmptyStrings.setPassword("");
        userWithEmptyStrings.setUsername("");

        doNothing().when(emailValidator).validate("");
        doNothing().when(phoneValidator).validate("");
        doNothing().when(passwordValidator).validate("");
        doNothing().when(usernameValidator).validate("");

        // Act
        registrationValidator.validate(userWithEmptyStrings);

        // Assert
        verify(emailValidator, times(1)).validate("");
        verify(phoneValidator, times(1)).validate("");
        verify(passwordValidator, times(1)).validate("");
        verify(usernameValidator, times(1)).validate("");
    }
}


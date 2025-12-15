package dk.via.sep3.model.utils.validation;

import dk.via.sep3.exceptionHandler.BusinessRuleViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EmailValidator
 * Tests email format validation
 */
class EmailValidatorTest {

    private EmailValidator emailValidator;

    @BeforeEach
    void setUp() {
        emailValidator = new EmailValidator();
    }

    @Test
    @DisplayName("Should accept valid email address")
    void testValidate_ValidEmail() {
        // Act & Assert - Should not throw exception
        assertDoesNotThrow(() -> emailValidator.validate("john.doe@example.com"));
    }

    @Test
    @DisplayName("Should accept email with plus sign")
    void testValidate_EmailWithPlusSign() {
        assertDoesNotThrow(() -> emailValidator.validate("john+test@example.com"));
    }

    @Test
    @DisplayName("Should accept email with underscores")
    void testValidate_EmailWithUnderscore() {
        assertDoesNotThrow(() -> emailValidator.validate("john_doe@example.com"));
    }

    @Test
    @DisplayName("Should accept email with hyphens")
    void testValidate_EmailWithHyphen() {
        assertDoesNotThrow(() -> emailValidator.validate("john-doe@example.com"));
    }

    @Test
    @DisplayName("Should accept email with numbers")
    void testValidate_EmailWithNumbers() {
        assertDoesNotThrow(() -> emailValidator.validate("john123@example.com"));
    }

    @Test
    @DisplayName("Should accept email with subdomain")
    void testValidate_EmailWithSubdomain() {
        assertDoesNotThrow(() -> emailValidator.validate("john@mail.example.com"));
    }

    @Test
    @DisplayName("Should accept email with long TLD")
    void testValidate_LongTLD() {
        assertDoesNotThrow(() -> emailValidator.validate("john@example.international"));
    }

    @Test
    @DisplayName("Should reject null email")
    void testValidate_NullEmail() {
        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
            BusinessRuleViolationException.class,
            () -> emailValidator.validate(null)
        );

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject empty email")
    void testValidate_EmptyEmail() {
        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
            BusinessRuleViolationException.class,
            () -> emailValidator.validate("")
        );

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject email without @ symbol")
    void testValidate_NoAtSymbol() {
        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
            BusinessRuleViolationException.class,
            () -> emailValidator.validate("johnexample.com")
        );

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject email without domain")
    void testValidate_NoDomain() {
        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
            BusinessRuleViolationException.class,
            () -> emailValidator.validate("john@")
        );

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject email without local part")
    void testValidate_NoLocalPart() {
        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
            BusinessRuleViolationException.class,
            () -> emailValidator.validate("@example.com")
        );

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject email without TLD")
    void testValidate_NoTLD() {
        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
            BusinessRuleViolationException.class,
            () -> emailValidator.validate("john@example")
        );

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject email with spaces")
    void testValidate_EmailWithSpaces() {
        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
            BusinessRuleViolationException.class,
            () -> emailValidator.validate("john doe@example.com")
        );

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject email with multiple @ symbols")
    void testValidate_MultipleAtSymbols() {
        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
            BusinessRuleViolationException.class,
            () -> emailValidator.validate("john@@example.com")
        );

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject email starting with dot")
    void testValidate_StartsWithDot() {
        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
            BusinessRuleViolationException.class,
            () -> emailValidator.validate(".john@example.com")
        );

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject email with consecutive dots")
    void testValidate_ConsecutiveDots() {
        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
            BusinessRuleViolationException.class,
            () -> emailValidator.validate("john..doe@example.com")
        );

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject email with TLD less than 2 characters")
    void testValidate_ShortTLD() {
        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
            BusinessRuleViolationException.class,
            () -> emailValidator.validate("john@example.c")
        );

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    @DisplayName("Should accept email with 2 character TLD")
    void testValidate_TwoCharTLD() {
        assertDoesNotThrow(() -> emailValidator.validate("john@example.uk"));
    }

    @Test
    @DisplayName("Should accept email with mixed case")
    void testValidate_MixedCase() {
        assertDoesNotThrow(() -> emailValidator.validate("John.Doe@Example.COM"));
    }

    @Test
    @DisplayName("Should reject email ending with dot")
    void testValidate_EndsWithDot() {
        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
            BusinessRuleViolationException.class,
            () -> emailValidator.validate("john@example.com.")
        );

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject email with special characters in domain")
    void testValidate_SpecialCharsInDomain() {
        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
            BusinessRuleViolationException.class,
            () -> emailValidator.validate("john@exam!ple.com")
        );

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    @DisplayName("Should accept common email providers")
    void testValidate_CommonProviders() {
        assertDoesNotThrow(() -> emailValidator.validate("user@gmail.com"));
        assertDoesNotThrow(() -> emailValidator.validate("user@yahoo.com"));
        assertDoesNotThrow(() -> emailValidator.validate("user@outlook.com"));
        assertDoesNotThrow(() -> emailValidator.validate("user@hotmail.com"));
    }

    @Test
    @DisplayName("Should accept corporate email format")
    void testValidate_CorporateEmail() {
        assertDoesNotThrow(() -> emailValidator.validate("firstname.lastname@company.co.uk"));
    }

    @Test
    @DisplayName("Should reject email with only whitespace")
    void testValidate_WhitespaceOnly() {
        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(
            BusinessRuleViolationException.class,
            () -> emailValidator.validate("   ")
        );

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    @DisplayName("Should accept email with percentage sign")
    void testValidate_PercentageSign() {
        assertDoesNotThrow(() -> emailValidator.validate("john%doe@example.com"));
    }

    @Test
    @DisplayName("Should accept very long email address")
    void testValidate_VeryLongEmail() {
        String longEmail = "verylongemailaddresswithlotsocharacters@subdomain.example.international";
        assertDoesNotThrow(() -> emailValidator.validate(longEmail));
    }
}


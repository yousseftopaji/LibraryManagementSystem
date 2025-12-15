package dk.via.sep3.shared.loan;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CreateLoanDTO
 * Tests DTO construction and field access
 */
class CreateLoanDTOTest {

    @Test
    @DisplayName("Should create CreateLoanDTO with constructor")
    void testConstructor() {
        // Act
        CreateLoanDTO dto = new CreateLoanDTO("johndoe", "978-0-123456-47-2");

        // Assert
        assertNotNull(dto);
        assertEquals("johndoe", dto.getUsername());
        assertEquals("978-0-123456-47-2", dto.getBookISBN());
    }

    @Test
    @DisplayName("Should set and get username")
    void testSetGetUsername() {
        // Arrange
        CreateLoanDTO dto = new CreateLoanDTO("user1", "ISBN-123");

        // Act
        dto.setUsername("user2");

        // Assert
        assertEquals("user2", dto.getUsername());
    }

    @Test
    @DisplayName("Should set and get book ISBN")
    void testSetGetBookISBN() {
        // Arrange
        CreateLoanDTO dto = new CreateLoanDTO("user", "ISBN-123");

        // Act
        dto.setBookISBN("ISBN-456");

        // Assert
        assertEquals("ISBN-456", dto.getBookISBN());
    }

    @Test
    @DisplayName("Should handle null values")
    void testNullValues() {
        // Act
        CreateLoanDTO dto = new CreateLoanDTO(null, null);

        // Assert
        assertNull(dto.getUsername());
        assertNull(dto.getBookISBN());
    }

    @Test
    @DisplayName("Should handle empty strings")
    void testEmptyStrings() {
        // Act
        CreateLoanDTO dto = new CreateLoanDTO("", "");

        // Assert
        assertEquals("", dto.getUsername());
        assertEquals("", dto.getBookISBN());
    }

    @Test
    @DisplayName("Should handle special characters in username")
    void testSpecialCharactersInUsername() {
        // Act
        CreateLoanDTO dto = new CreateLoanDTO("user@example.com", "ISBN-123");

        // Assert
        assertEquals("user@example.com", dto.getUsername());
    }

    @Test
    @DisplayName("Should handle different ISBN formats")
    void testDifferentISBNFormats() {
        // ISBN-13
        CreateLoanDTO dto1 = new CreateLoanDTO("user", "978-0-123456-47-2");
        assertEquals("978-0-123456-47-2", dto1.getBookISBN());

        // ISBN-10
        CreateLoanDTO dto2 = new CreateLoanDTO("user", "0-123456-47-2");
        assertEquals("0-123456-47-2", dto2.getBookISBN());

        // Without hyphens
        CreateLoanDTO dto3 = new CreateLoanDTO("user", "9780123456472");
        assertEquals("9780123456472", dto3.getBookISBN());
    }
}


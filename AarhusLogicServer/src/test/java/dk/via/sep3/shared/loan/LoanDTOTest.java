package dk.via.sep3.shared.loan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LoanDTO
 * Tests DTO construction and field access
 */
class LoanDTOTest {

    private LoanDTO loanDTO;

    @BeforeEach
    void setUp() {
        loanDTO = new LoanDTO();
    }

    @Test
    @DisplayName("Should create LoanDTO with no-arg constructor")
    void testNoArgConstructor() {
        // Act
        LoanDTO dto = new LoanDTO();

        // Assert
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getBorrowDate());
        assertNull(dto.getDueDate());
        assertFalse(dto.isReturned());
        assertEquals(0, dto.getNumberOfExtensions());
        assertNull(dto.getUsername());
        assertEquals(0, dto.getBookId());
    }

    @Test
    @DisplayName("Should create LoanDTO with all args constructor")
    void testAllArgsConstructor() {
        // Act
        LoanDTO dto = new LoanDTO(
            "123",
            "2025-01-01",
            "2025-01-31",
            false,
            2,
            "johndoe",
            456
        );

        // Assert
        assertEquals("123", dto.getId());
        assertEquals("2025-01-01", dto.getBorrowDate());
        assertEquals("2025-01-31", dto.getDueDate());
        assertFalse(dto.isReturned());
        assertEquals(2, dto.getNumberOfExtensions());
        assertEquals("johndoe", dto.getUsername());
        assertEquals(456, dto.getBookId());
    }

    @Test
    @DisplayName("Should set and get id")
    void testSetGetId() {
        // Act
        loanDTO.setId("789");

        // Assert
        assertEquals("789", loanDTO.getId());
    }

    @Test
    @DisplayName("Should set and get borrow date")
    void testSetGetBorrowDate() {
        // Act
        loanDTO.setBorrowDate("2025-06-15");

        // Assert
        assertEquals("2025-06-15", loanDTO.getBorrowDate());
    }

    @Test
    @DisplayName("Should set and get due date")
    void testSetGetDueDate() {
        // Act
        loanDTO.setDueDate("2025-07-15");

        // Assert
        assertEquals("2025-07-15", loanDTO.getDueDate());
    }

    @Test
    @DisplayName("Should set and get returned status")
    void testSetGetReturned() {
        // Act - Set to true
        loanDTO.setReturned(true);

        // Assert
        assertTrue(loanDTO.isReturned());

        // Act - Set to false
        loanDTO.setReturned(false);

        // Assert
        assertFalse(loanDTO.isReturned());
    }

    @Test
    @DisplayName("Should set and get number of extensions")
    void testSetGetNumberOfExtensions() {
        // Act
        loanDTO.setNumberOfExtensions(5);

        // Assert
        assertEquals(5, loanDTO.getNumberOfExtensions());
    }

    @Test
    @DisplayName("Should set and get username")
    void testSetGetUsername() {
        // Act
        loanDTO.setUsername("janedoe");

        // Assert
        assertEquals("janedoe", loanDTO.getUsername());
    }

    @Test
    @DisplayName("Should set and get book id")
    void testSetGetBookId() {
        // Act
        loanDTO.setBookId(999);

        // Assert
        assertEquals(999, loanDTO.getBookId());
    }

    @Test
    @DisplayName("Should handle null string values")
    void testNullStringValues() {
        // Act
        loanDTO.setId(null);
        loanDTO.setBorrowDate(null);
        loanDTO.setDueDate(null);
        loanDTO.setUsername(null);

        // Assert
        assertNull(loanDTO.getId());
        assertNull(loanDTO.getBorrowDate());
        assertNull(loanDTO.getDueDate());
        assertNull(loanDTO.getUsername());
    }

    @Test
    @DisplayName("Should handle empty string values")
    void testEmptyStringValues() {
        // Act
        loanDTO.setId("");
        loanDTO.setBorrowDate("");
        loanDTO.setDueDate("");
        loanDTO.setUsername("");

        // Assert
        assertEquals("", loanDTO.getId());
        assertEquals("", loanDTO.getBorrowDate());
        assertEquals("", loanDTO.getDueDate());
        assertEquals("", loanDTO.getUsername());
    }

    @Test
    @DisplayName("Should handle zero extensions")
    void testZeroExtensions() {
        // Act
        loanDTO.setNumberOfExtensions(0);

        // Assert
        assertEquals(0, loanDTO.getNumberOfExtensions());
    }

    @Test
    @DisplayName("Should handle maximum extensions")
    void testMaximumExtensions() {
        // Act
        loanDTO.setNumberOfExtensions(12);

        // Assert
        assertEquals(12, loanDTO.getNumberOfExtensions());
    }

    @Test
    @DisplayName("Should handle negative extensions")
    void testNegativeExtensions() {
        // Act
        loanDTO.setNumberOfExtensions(-1);

        // Assert
        assertEquals(-1, loanDTO.getNumberOfExtensions());
    }

    @Test
    @DisplayName("Should handle large book id")
    void testLargeBookId() {
        // Act
        loanDTO.setBookId(Integer.MAX_VALUE);

        // Assert
        assertEquals(Integer.MAX_VALUE, loanDTO.getBookId());
    }

    @Test
    @DisplayName("Should handle negative book id")
    void testNegativeBookId() {
        // Act
        loanDTO.setBookId(-1);

        // Assert
        assertEquals(-1, loanDTO.getBookId());
    }

    @Test
    @DisplayName("Should handle zero book id")
    void testZeroBookId() {
        // Act
        loanDTO.setBookId(0);

        // Assert
        assertEquals(0, loanDTO.getBookId());
    }

    @Test
    @DisplayName("Should preserve all fields in constructor")
    void testConstructor_PreservesAllFields() {
        // Act
        LoanDTO dto = new LoanDTO(
            "100",
            "2025-01-15",
            "2025-02-14",
            true,
            3,
            "testuser",
            200
        );

        // Assert
        assertEquals("100", dto.getId());
        assertEquals("2025-01-15", dto.getBorrowDate());
        assertEquals("2025-02-14", dto.getDueDate());
        assertTrue(dto.isReturned());
        assertEquals(3, dto.getNumberOfExtensions());
        assertEquals("testuser", dto.getUsername());
        assertEquals(200, dto.getBookId());
    }

    @Test
    @DisplayName("Should handle date format variations")
    void testDifferentDateFormats() {
        // ISO format
        loanDTO.setBorrowDate("2025-12-25");
        assertEquals("2025-12-25", loanDTO.getBorrowDate());

        // US format
        loanDTO.setBorrowDate("12/25/2025");
        assertEquals("12/25/2025", loanDTO.getBorrowDate());

        // European format
        loanDTO.setBorrowDate("25-12-2025");
        assertEquals("25-12-2025", loanDTO.getBorrowDate());
    }

    @Test
    @DisplayName("Should handle special characters in username")
    void testSpecialCharactersInUsername() {
        // Act
        loanDTO.setUsername("user@name.com");

        // Assert
        assertEquals("user@name.com", loanDTO.getUsername());
    }

    @Test
    @DisplayName("Should handle very long username")
    void testVeryLongUsername() {
        // Arrange
        String longUsername = "a".repeat(1000);

        // Act
        loanDTO.setUsername(longUsername);

        // Assert
        assertEquals(longUsername, loanDTO.getUsername());
    }

    @Test
    @DisplayName("Should handle numeric string id")
    void testNumericStringId() {
        // Act
        loanDTO.setId("123456789");

        // Assert
        assertEquals("123456789", loanDTO.getId());
    }

    @Test
    @DisplayName("Should handle alphanumeric string id")
    void testAlphanumericStringId() {
        // Act
        loanDTO.setId("LOAN-123-ABC");

        // Assert
        assertEquals("LOAN-123-ABC", loanDTO.getId());
    }

    @Test
    @DisplayName("Should toggle returned status")
    void testToggleReturnedStatus() {
        // Initially false
        assertFalse(loanDTO.isReturned());

        // Toggle to true
        loanDTO.setReturned(true);
        assertTrue(loanDTO.isReturned());

        // Toggle back to false
        loanDTO.setReturned(false);
        assertFalse(loanDTO.isReturned());
    }

    @Test
    @DisplayName("Should handle loan with no extensions")
    void testLoanWithNoExtensions() {
        // Arrange
        LoanDTO dto = new LoanDTO(
            "1",
            "2025-01-01",
            "2025-01-31",
            false,
            0,
            "user",
            1
        );

        // Assert
        assertEquals(0, dto.getNumberOfExtensions());
        assertFalse(dto.isReturned());
    }

    @Test
    @DisplayName("Should handle returned loan")
    void testReturnedLoan() {
        // Arrange
        LoanDTO dto = new LoanDTO(
            "1",
            "2025-01-01",
            "2025-01-31",
            true,
            0,
            "user",
            1
        );

        // Assert
        assertTrue(dto.isReturned());
    }

    @Test
    @DisplayName("Should handle loan with extensions")
    void testLoanWithExtensions() {
        // Arrange
        LoanDTO dto = new LoanDTO(
            "1",
            "2025-01-01",
            "2025-04-01",
            false,
            3,
            "user",
            1
        );

        // Assert
        assertEquals(3, dto.getNumberOfExtensions());
        assertFalse(dto.isReturned());
    }
}


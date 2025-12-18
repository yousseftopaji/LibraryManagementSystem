package dk.via.sep3.application.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Loan domain class
 * Tests domain object construction and field access
 */
class LoanTest {

    private Loan loan;
    private Date borrowDate;
    private Date dueDate;

    @BeforeEach
    void setUp() {
        loan = new Loan();
        borrowDate = Date.valueOf(LocalDate.now());
        dueDate = Date.valueOf(LocalDate.now().plusDays(30));
    }

    @Test
    @DisplayName("Should create Loan with no-arg constructor")
    void testNoArgConstructor() {
        // Act
        Loan l = new Loan();

        // Assert
        assertNotNull(l);
        assertEquals(0, l.getLoanId());
        assertEquals(0, l.getBookId());
        assertNull(l.getUsername());
        assertNull(l.getBookISBN());
        assertNull(l.getBorrowDate());
        assertNull(l.getDueDate());
        assertFalse(l.isReturned());
        assertEquals(0, l.getNumberOfExtensions());
    }

    @Test
    @DisplayName("Should create Loan with all args constructor")
    void testAllArgsConstructor() {
        // Act
        Loan l = new Loan(
            1,
            100,
            "johndoe",
            borrowDate,
            dueDate,
            false,
            2
        );

        // Assert
        assertEquals(1, l.getLoanId());
        assertEquals(100, l.getBookId());
        assertEquals("johndoe", l.getUsername());
        assertEquals(borrowDate, l.getBorrowDate());
        assertEquals(dueDate, l.getDueDate());
        assertFalse(l.isReturned());
        assertEquals(2, l.getNumberOfExtensions());
    }

    @Test
    @DisplayName("Should set and get loan id")
    void testSetGetLoanId() {
        // Act
        loan.setLoanId(123);

        // Assert
        assertEquals(123, loan.getLoanId());
    }

    @Test
    @DisplayName("Should set and get book id")
    void testSetGetBookId() {
        // Act
        loan.setBookId(456);

        // Assert
        assertEquals(456, loan.getBookId());
    }

    @Test
    @DisplayName("Should set and get username")
    void testSetGetUsername() {
        // Act
        loan.setUsername("janedoe");

        // Assert
        assertEquals("janedoe", loan.getUsername());
    }

    @Test
    @DisplayName("Should set and get book ISBN")
    void testSetGetBookISBN() {
        // Act
        loan.setBookISBN("978-0-123456-47-2");

        // Assert
        assertEquals("978-0-123456-47-2", loan.getBookISBN());
    }

    @Test
    @DisplayName("Should set and get borrow date")
    void testSetGetBorrowDate() {
        // Act
        loan.setBorrowDate(borrowDate);

        // Assert
        assertEquals(borrowDate, loan.getBorrowDate());
    }

    @Test
    @DisplayName("Should set and get due date")
    void testSetGetDueDate() {
        // Act
        loan.setDueDate(dueDate);

        // Assert
        assertEquals(dueDate, loan.getDueDate());
    }

    @Test
    @DisplayName("Should set and get returned status")
    void testSetGetReturned() {
        // Act - Set to true
        loan.setReturned(true);
        assertTrue(loan.isReturned());

        // Act - Set to false
        loan.setReturned(false);
        assertFalse(loan.isReturned());
    }

    @Test
    @DisplayName("Should set and get number of extensions")
    void testSetGetNumberOfExtensions() {
        // Act
        loan.setNumberOfExtensions(5);

        // Assert
        assertEquals(5, loan.getNumberOfExtensions());
    }

    @Test
    @DisplayName("Should handle null username")
    void testNullUsername() {
        // Act
        loan.setUsername(null);

        // Assert
        assertNull(loan.getUsername());
    }

    @Test
    @DisplayName("Should handle null ISBN")
    void testNullISBN() {
        // Act
        loan.setBookISBN(null);

        // Assert
        assertNull(loan.getBookISBN());
    }

    @Test
    @DisplayName("Should handle null dates")
    void testNullDates() {
        // Act
        loan.setBorrowDate(null);
        loan.setDueDate(null);

        // Assert
        assertNull(loan.getBorrowDate());
        assertNull(loan.getDueDate());
    }

    @Test
    @DisplayName("Should handle zero extensions")
    void testZeroExtensions() {
        // Act
        loan.setNumberOfExtensions(0);

        // Assert
        assertEquals(0, loan.getNumberOfExtensions());
    }

    @Test
    @DisplayName("Should handle maximum extensions")
    void testMaximumExtensions() {
        // Act
        loan.setNumberOfExtensions(12);

        // Assert
        assertEquals(12, loan.getNumberOfExtensions());
    }

    @Test
    @DisplayName("Should handle negative loan id")
    void testNegativeLoanId() {
        // Act
        loan.setLoanId(-1);

        // Assert
        assertEquals(-1, loan.getLoanId());
    }

    @Test
    @DisplayName("Should handle negative book id")
    void testNegativeBookId() {
        // Act
        loan.setBookId(-1);

        // Assert
        assertEquals(-1, loan.getBookId());
    }

    @Test
    @DisplayName("Should handle large ids")
    void testLargeIds() {
        // Act
        loan.setLoanId(Integer.MAX_VALUE);
        loan.setBookId(Integer.MAX_VALUE);

        // Assert
        assertEquals(Integer.MAX_VALUE, loan.getLoanId());
        assertEquals(Integer.MAX_VALUE, loan.getBookId());
    }

    @Test
    @DisplayName("Should handle dates in past")
    void testDatesInPast() {
        // Arrange
        Date pastDate = Date.valueOf(LocalDate.now().minusDays(10));

        // Act
        loan.setBorrowDate(pastDate);

        // Assert
        assertEquals(pastDate, loan.getBorrowDate());
    }

    @Test
    @DisplayName("Should handle dates in future")
    void testDatesInFuture() {
        // Arrange
        Date futureDate = Date.valueOf(LocalDate.now().plusDays(60));

        // Act
        loan.setDueDate(futureDate);

        // Assert
        assertEquals(futureDate, loan.getDueDate());
    }

    @Test
    @DisplayName("Should toggle returned status")
    void testToggleReturnedStatus() {
        // Initially false
        assertFalse(loan.isReturned());

        // Toggle to true
        loan.setReturned(true);
        assertTrue(loan.isReturned());

        // Toggle back to false
        loan.setReturned(false);
        assertFalse(loan.isReturned());
    }

    @Test
    @DisplayName("Should preserve all fields in constructor")
    void testConstructor_PreservesAllFields() {
        // Act
        Loan l = new Loan(
            99,
            88,
            "testuser",
            borrowDate,
            dueDate,
            true,
            3
        );

        // Assert
        assertEquals(99, l.getLoanId());
        assertEquals(88, l.getBookId());
        assertEquals("testuser", l.getUsername());
        assertEquals(borrowDate, l.getBorrowDate());
        assertEquals(dueDate, l.getDueDate());
        assertTrue(l.isReturned());
        assertEquals(3, l.getNumberOfExtensions());
    }

    @Test
    @DisplayName("Should handle empty username")
    void testEmptyUsername() {
        // Act
        loan.setUsername("");

        // Assert
        assertEquals("", loan.getUsername());
    }

    @Test
    @DisplayName("Should handle empty ISBN")
    void testEmptyISBN() {
        // Act
        loan.setBookISBN("");

        // Assert
        assertEquals("", loan.getBookISBN());
    }

    @Test
    @DisplayName("Should handle special characters in username")
    void testSpecialCharactersInUsername() {
        // Act
        loan.setUsername("user@example.com");

        // Assert
        assertEquals("user@example.com", loan.getUsername());
    }

    @Test
    @DisplayName("Should handle different ISBN formats")
    void testDifferentISBNFormats() {
        // ISBN-13
        loan.setBookISBN("978-0-123456-47-2");
        assertEquals("978-0-123456-47-2", loan.getBookISBN());

        // ISBN-10
        loan.setBookISBN("0-123456-47-2");
        assertEquals("0-123456-47-2", loan.getBookISBN());

        // Without hyphens
        loan.setBookISBN("9780123456472");
        assertEquals("9780123456472", loan.getBookISBN());
    }

    @Test
    @DisplayName("Should handle loan with no extensions")
    void testLoanWithNoExtensions() {
        // Arrange
        Loan l = new Loan(1, 1, "user", borrowDate, dueDate, false, 0);

        // Assert
        assertEquals(0, l.getNumberOfExtensions());
        assertFalse(l.isReturned());
    }

    @Test
    @DisplayName("Should handle returned loan")
    void testReturnedLoan() {
        // Arrange
        Loan l = new Loan(1, 1, "user", borrowDate, dueDate, true, 0);

        // Assert
        assertTrue(l.isReturned());
    }

    @Test
    @DisplayName("Should handle loan with multiple extensions")
    void testLoanWithMultipleExtensions() {
        // Arrange
        Loan l = new Loan(1, 1, "user", borrowDate, dueDate, false, 5);

        // Assert
        assertEquals(5, l.getNumberOfExtensions());
    }

    @Test
    @DisplayName("Should handle same borrow and due date")
    void testSameBorrowAndDueDate() {
        // Arrange
        Date sameDate = Date.valueOf(LocalDate.now());

        // Act
        loan.setBorrowDate(sameDate);
        loan.setDueDate(sameDate);

        // Assert
        assertEquals(sameDate, loan.getBorrowDate());
        assertEquals(sameDate, loan.getDueDate());
    }
}


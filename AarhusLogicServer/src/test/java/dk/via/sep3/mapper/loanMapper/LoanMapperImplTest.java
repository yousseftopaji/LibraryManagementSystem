package dk.via.sep3.mapper.loanMapper;

import dk.via.sep3.DTOLoan;
import dk.via.sep3.model.domain.Loan;
import dk.via.sep3.shared.extension.CreateExtensionDTO;
import dk.via.sep3.shared.loan.CreateLoanDTO;
import dk.via.sep3.shared.loan.LoanDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class LoanMapperImplTest {

    private LoanMapperImpl loanMapper;

    @BeforeEach
    void setUp() {
        loanMapper = new LoanMapperImpl();
    }

    @Test
    @DisplayName("Should map CreateLoanDTO to Loan domain")
    void testMapCreateLoanDTOToDomain() {
        // Arrange
        CreateLoanDTO createLoanDTO = new CreateLoanDTO("testuser", "123456");

        // Act
        Loan result = loanMapper.mapCreateLoanDTOToDomain(createLoanDTO);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("123456", result.getBookISBN());
    }

    @Test
    @DisplayName("Should map Loan domain to DTOLoan")
    void testMapDomainToDTOLoan() {
        // Arrange
        Loan loan = new Loan();
        loan.setLoanId(1);
        loan.setBorrowDate(Date.valueOf("2025-01-01"));
        loan.setDueDate(Date.valueOf("2025-01-31"));
        loan.setUsername("testuser");
        loan.setReturned(false);
        loan.setBookId(100);
        loan.setNumberOfExtensions(0);

        // Act
        DTOLoan result = loanMapper.mapDomainToDTOLoan(loan);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("2025-01-01", result.getBorrowDate());
        assertEquals("2025-01-31", result.getDueDate());
        assertEquals("testuser", result.getUsername());
        assertFalse(result.getIsReturned());
        assertEquals(100, result.getBookId());
        assertEquals(0, result.getNumberOfExtensions());
    }

    @Test
    @DisplayName("Should map DTOLoan to Loan domain")
    void testMapDTOLoanToDomain() {
        // Arrange
        DTOLoan dtoLoan = DTOLoan.newBuilder()
            .setId(1)
            .setBorrowDate("2025-01-01")
            .setDueDate("2025-01-31")
            .setUsername("testuser")
            .setIsReturned(false)
            .setBookId(100)
            .setNumberOfExtensions(0)
            .build();

        // Act
        Loan result = loanMapper.mapDTOLoanToDomain(dtoLoan);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getLoanId());
        assertEquals(Date.valueOf("2025-01-01"), result.getBorrowDate());
        assertEquals(Date.valueOf("2025-01-31"), result.getDueDate());
        assertEquals("testuser", result.getUsername());
        assertFalse(result.isReturned());
        assertEquals(100, result.getBookId());
        assertEquals(0, result.getNumberOfExtensions());
    }

    @Test
    @DisplayName("Should map Loan domain to LoanDTO")
    void testMapDomainToLoanDTO() {
        // Arrange
        Loan loan = new Loan();
        loan.setLoanId(1);
        loan.setBorrowDate(Date.valueOf("2025-01-01"));
        loan.setDueDate(Date.valueOf("2025-01-31"));
        loan.setReturned(false);
        loan.setNumberOfExtensions(2);
        loan.setUsername("testuser");
        loan.setBookId(100);

        // Act
        LoanDTO result = loanMapper.mapDomainToLoanDTO(loan);

        // Assert
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("2025-01-01", result.getBorrowDate());
        assertEquals("2025-01-31", result.getDueDate());
        assertFalse(result.isReturned());
        assertEquals(2, result.getNumberOfExtensions());
        assertEquals("testuser", result.getUsername());
        assertEquals(100, result.getBookId());
    }

    @Test
    @DisplayName("Should map CreateExtensionDTO to Loan domain")
    void testMapCreateExtensionDTOToDomain() {
        // Arrange
        CreateExtensionDTO createExtensionDTO = new CreateExtensionDTO(1, "testuser");

        // Act
        Loan result = loanMapper.mapCreateExtensionDTOToDomain(createExtensionDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getLoanId());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    @DisplayName("Should handle loan with extensions")
    void testMapDomainToDTOLoan_WithExtensions() {
        // Arrange
        Loan loan = new Loan();
        loan.setLoanId(1);
        loan.setBorrowDate(Date.valueOf("2025-01-01"));
        loan.setDueDate(Date.valueOf("2025-03-31"));
        loan.setUsername("testuser");
        loan.setReturned(false);
        loan.setBookId(100);
        loan.setNumberOfExtensions(5);

        // Act
        DTOLoan result = loanMapper.mapDomainToDTOLoan(loan);

        // Assert
        assertEquals(5, result.getNumberOfExtensions());
    }

    @Test
    @DisplayName("Should handle returned loan")
    void testMapDomainToDTOLoan_ReturnedLoan() {
        // Arrange
        Loan loan = new Loan();
        loan.setLoanId(1);
        loan.setBorrowDate(Date.valueOf("2025-01-01"));
        loan.setDueDate(Date.valueOf("2025-01-31"));
        loan.setUsername("testuser");
        loan.setReturned(true);
        loan.setBookId(100);
        loan.setNumberOfExtensions(0);

        // Act
        DTOLoan result = loanMapper.mapDomainToDTOLoan(loan);

        // Assert
        assertTrue(result.getIsReturned());
    }

    @Test
    @DisplayName("Should preserve all fields in round-trip conversion")
    void testRoundTripConversion() {
        // Arrange
        Loan original = new Loan();
        original.setLoanId(99);
        original.setBorrowDate(Date.valueOf("2025-06-15"));
        original.setDueDate(Date.valueOf("2025-07-15"));
        original.setUsername("roundtripuser");
        original.setReturned(false);
        original.setBookId(999);
        original.setNumberOfExtensions(3);

        // Act
        DTOLoan dtoLoan = loanMapper.mapDomainToDTOLoan(original);
        Loan converted = loanMapper.mapDTOLoanToDomain(dtoLoan);

        // Assert
        assertEquals(original.getLoanId(), converted.getLoanId());
        assertEquals(original.getBorrowDate(), converted.getBorrowDate());
        assertEquals(original.getDueDate(), converted.getDueDate());
        assertEquals(original.getUsername(), converted.getUsername());
        assertEquals(original.isReturned(), converted.isReturned());
        assertEquals(original.getBookId(), converted.getBookId());
        assertEquals(original.getNumberOfExtensions(), converted.getNumberOfExtensions());
    }

    @Test
    @DisplayName("Should handle different date formats")
    void testMapDTOLoanToDomain_DifferentDates() {
        // Arrange
        DTOLoan dtoLoan = DTOLoan.newBuilder()
            .setId(1)
            .setBorrowDate("2025-12-25")
            .setDueDate("2026-01-25")
            .setUsername("testuser")
            .setIsReturned(false)
            .setBookId(100)
            .setNumberOfExtensions(0)
            .build();

        // Act
        Loan result = loanMapper.mapDTOLoanToDomain(dtoLoan);

        // Assert
        assertEquals(Date.valueOf("2025-12-25"), result.getBorrowDate());
        assertEquals(Date.valueOf("2026-01-25"), result.getDueDate());
    }

    @Test
    @DisplayName("Should map LoanDTO with string ID correctly")
    void testMapDomainToLoanDTO_StringIdConversion() {
        // Arrange
        Loan loan = new Loan();
        loan.setLoanId(12345);
        loan.setBorrowDate(Date.valueOf("2025-01-01"));
        loan.setDueDate(Date.valueOf("2025-01-31"));
        loan.setReturned(false);
        loan.setNumberOfExtensions(0);
        loan.setUsername("testuser");
        loan.setBookId(100);

        // Act
        LoanDTO result = loanMapper.mapDomainToLoanDTO(loan);

        // Assert
        assertEquals("12345", result.getId());
    }

    @Test
    @DisplayName("Should handle CreateLoanDTO with different ISBNs")
    void testMapCreateLoanDTOToDomain_DifferentISBNs() {
        // Test standard ISBN
        CreateLoanDTO dto1 = new CreateLoanDTO("user1", "1234567890");
        Loan result1 = loanMapper.mapCreateLoanDTOToDomain(dto1);
        assertEquals("1234567890", result1.getBookISBN());

        // Test ISBN-13
        CreateLoanDTO dto2 = new CreateLoanDTO("user2", "978-0-123456-78-9");
        Loan result2 = loanMapper.mapCreateLoanDTOToDomain(dto2);
        assertEquals("978-0-123456-78-9", result2.getBookISBN());
    }
}


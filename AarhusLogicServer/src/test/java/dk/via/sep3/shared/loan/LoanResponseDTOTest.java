package dk.via.sep3.shared.loan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoanResponseDTOTest {

    private LoanResponseDTO loanResponseDTO;

    @BeforeEach
    void setUp() {
        loanResponseDTO = new LoanResponseDTO();
    }

    @Test
    @DisplayName("Should create LoanResponseDTO with no-arg constructor")
    void testNoArgConstructor() {
        // Act
        LoanResponseDTO dto = new LoanResponseDTO();

        // Assert
        assertNotNull(dto);
        assertNull(dto.getLoan());
        assertNull(dto.getUsername());
    }

    @Test
    @DisplayName("Should create LoanResponseDTO with constructor")
    void testConstructorWithParams() {
        // Arrange
        LoanDTO loanDTO = new LoanDTO("1", "2025-01-01", "2025-01-31", false, 0, "testuser", 100);

        // Act
        LoanResponseDTO dto = new LoanResponseDTO(loanDTO, "testuser");

        // Assert
        assertNotNull(dto);
        assertEquals(loanDTO, dto.getLoan());
        assertEquals("testuser", dto.getUsername());
    }

    @Test
    @DisplayName("Should set and get loan")
    void testSetGetLoan() {
        // Arrange
        LoanDTO loanDTO = new LoanDTO("1", "2025-01-01", "2025-01-31", false, 0, "testuser", 100);

        // Act
        loanResponseDTO.setLoan(loanDTO);

        // Assert
        assertEquals(loanDTO, loanResponseDTO.getLoan());
    }

    @Test
    @DisplayName("Should set and get username")
    void testSetGetUsername() {
        // Act
        loanResponseDTO.setUsername("testuser");

        // Assert
        assertEquals("testuser", loanResponseDTO.getUsername());
    }

    @Test
    @DisplayName("Should handle null loan")
    void testNullLoan() {
        // Act
        loanResponseDTO.setLoan(null);

        // Assert
        assertNull(loanResponseDTO.getLoan());
    }

    @Test
    @DisplayName("Should handle null username")
    void testNullUsername() {
        // Act
        loanResponseDTO.setUsername(null);

        // Assert
        assertNull(loanResponseDTO.getUsername());
    }

    @Test
    @DisplayName("Should handle empty username")
    void testEmptyUsername() {
        // Act
        loanResponseDTO.setUsername("");

        // Assert
        assertEquals("", loanResponseDTO.getUsername());
    }

    @Test
    @DisplayName("Should preserve loan data")
    void testPreserveLoanData() {
        // Arrange
        LoanDTO loanDTO = new LoanDTO("99", "2025-06-15", "2025-07-15", false, 3, "user@example.com", 999);

        // Act
        loanResponseDTO.setLoan(loanDTO);

        // Assert
        assertNotNull(loanResponseDTO.getLoan());
        assertEquals("99", loanResponseDTO.getLoan().getId());
        assertEquals("2025-06-15", loanResponseDTO.getLoan().getBorrowDate());
        assertEquals("2025-07-15", loanResponseDTO.getLoan().getDueDate());
        assertEquals("user@example.com", loanResponseDTO.getLoan().getUsername());
    }

    @Test
    @DisplayName("Should update loan")
    void testUpdateLoan() {
        // Arrange
        LoanDTO loan1 = new LoanDTO("1", "2025-01-01", "2025-01-31", false, 0, "user1", 100);
        LoanDTO loan2 = new LoanDTO("2", "2025-02-01", "2025-02-28", false, 0, "user2", 200);

        // Act
        loanResponseDTO.setLoan(loan1);
        loanResponseDTO.setLoan(loan2);

        // Assert
        assertEquals(loan2, loanResponseDTO.getLoan());
        assertEquals("2", loanResponseDTO.getLoan().getId());
    }

    @Test
    @DisplayName("Should update username")
    void testUpdateUsername() {
        // Act
        loanResponseDTO.setUsername("user1");
        loanResponseDTO.setUsername("user2");

        // Assert
        assertEquals("user2", loanResponseDTO.getUsername());
    }

    @Test
    @DisplayName("Should handle username with special characters")
    void testUsernameWithSpecialCharacters() {
        // Act
        loanResponseDTO.setUsername("user@example.com");

        // Assert
        assertEquals("user@example.com", loanResponseDTO.getUsername());
    }

    @Test
    @DisplayName("Should preserve all fields with constructor")
    void testConstructorPreservesFields() {
        // Arrange
        LoanDTO loanDTO = new LoanDTO("1", "2025-01-01", "2025-01-31", true, 5, "testuser", 100);
        String username = "responseuser";

        // Act
        LoanResponseDTO dto = new LoanResponseDTO(loanDTO, username);

        // Assert
        assertEquals(loanDTO, dto.getLoan());
        assertEquals(username, dto.getUsername());
        assertEquals("1", dto.getLoan().getId());
        assertTrue(dto.getLoan().isReturned());
        assertEquals(5, dto.getLoan().getNumberOfExtensions());
    }
}


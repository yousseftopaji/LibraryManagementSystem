package dk.via.sep3.shared.reservation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CreateReservationDTO
 * Tests DTO construction and field access
 */
class CreateReservationDTOTest {

    @Test
    @DisplayName("Should create CreateReservationDTO with constructor")
    void testConstructor() {
        // Act
        CreateReservationDTO dto = new CreateReservationDTO("johndoe", "978-0-123456-47-2");

        // Assert
        assertNotNull(dto);
        assertEquals("johndoe", dto.getUsername());
        assertEquals("978-0-123456-47-2", dto.getBookISBN());
    }

    @Test
    @DisplayName("Should set and get username")
    void testSetGetUsername() {
        // Arrange
        CreateReservationDTO dto = new CreateReservationDTO("user1", "ISBN-123");

        // Act
        dto.setUsername("user2");

        // Assert
        assertEquals("user2", dto.getUsername());
    }

    @Test
    @DisplayName("Should set and get book ISBN")
    void testSetGetBookISBN() {
        // Arrange
        CreateReservationDTO dto = new CreateReservationDTO("user", "ISBN-123");

        // Act
        dto.setBookISBN("ISBN-456");

        // Assert
        assertEquals("ISBN-456", dto.getBookISBN());
    }

    @Test
    @DisplayName("Should handle null values")
    void testNullValues() {
        // Act
        CreateReservationDTO dto = new CreateReservationDTO(null, null);

        // Assert
        assertNull(dto.getUsername());
        assertNull(dto.getBookISBN());
    }

    @Test
    @DisplayName("Should handle empty strings")
    void testEmptyStrings() {
        // Act
        CreateReservationDTO dto = new CreateReservationDTO("", "");

        // Assert
        assertEquals("", dto.getUsername());
        assertEquals("", dto.getBookISBN());
    }

    @Test
    @DisplayName("Should handle special characters in username")
    void testSpecialCharactersInUsername() {
        // Act
        CreateReservationDTO dto = new CreateReservationDTO("user@example.com", "ISBN-123");

        // Assert
        assertEquals("user@example.com", dto.getUsername());
    }

    @Test
    @DisplayName("Should handle different ISBN formats")
    void testDifferentISBNFormats() {
        // ISBN-13
        CreateReservationDTO dto1 = new CreateReservationDTO("user", "978-0-123456-47-2");
        assertEquals("978-0-123456-47-2", dto1.getBookISBN());

        // ISBN-10
        CreateReservationDTO dto2 = new CreateReservationDTO("user", "0-123456-47-2");
        assertEquals("0-123456-47-2", dto2.getBookISBN());

        // Without hyphens
        CreateReservationDTO dto3 = new CreateReservationDTO("user", "9780123456472");
        assertEquals("9780123456472", dto3.getBookISBN());
    }

    @Test
    @DisplayName("Should preserve values after creation")
    void testPreserveValuesAfterCreation() {
        // Arrange
        String username = "testuser";
        String isbn = "ISBN-TEST-123";

        // Act
        CreateReservationDTO dto = new CreateReservationDTO(username, isbn);

        // Assert
        assertEquals(username, dto.getUsername());
        assertEquals(isbn, dto.getBookISBN());
    }

    @Test
    @DisplayName("Should allow modification after creation")
    void testModificationAfterCreation() {
        // Arrange
        CreateReservationDTO dto = new CreateReservationDTO("user1", "ISBN-1");

        // Act
        dto.setUsername("user2");
        dto.setBookISBN("ISBN-2");

        // Assert
        assertEquals("user2", dto.getUsername());
        assertEquals("ISBN-2", dto.getBookISBN());
    }

    @Test
    @DisplayName("Should handle very long username")
    void testVeryLongUsername() {
        // Arrange
        String longUsername = "a".repeat(1000);

        // Act
        CreateReservationDTO dto = new CreateReservationDTO(longUsername, "ISBN-123");

        // Assert
        assertEquals(longUsername, dto.getUsername());
    }

    @Test
    @DisplayName("Should handle very long ISBN")
    void testVeryLongISBN() {
        // Arrange
        String longISBN = "ISBN-" + "1".repeat(100);

        // Act
        CreateReservationDTO dto = new CreateReservationDTO("user", longISBN);

        // Assert
        assertEquals(longISBN, dto.getBookISBN());
    }
}


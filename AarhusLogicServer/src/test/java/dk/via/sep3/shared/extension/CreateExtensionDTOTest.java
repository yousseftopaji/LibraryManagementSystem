package dk.via.sep3.shared.extension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CreateExtensionDTO
 * Tests DTO construction and field access
 */
class CreateExtensionDTOTest {

    @Test
    @DisplayName("Should create CreateExtensionDTO with constructor")
    void testConstructor() {
        // Act
        CreateExtensionDTO dto = new CreateExtensionDTO(123, "johndoe");

        // Assert
        assertNotNull(dto);
        assertEquals(123, dto.getLoanId());
        assertEquals("johndoe", dto.getUsername());
    }

    @Test
    @DisplayName("Should create CreateExtensionDTO with no-arg constructor")
    void testNoArgConstructor() {
        // Act
        CreateExtensionDTO dto = new CreateExtensionDTO();

        // Assert
        assertNotNull(dto);
        assertEquals(0, dto.getLoanId());
        assertNull(dto.getUsername());
    }

    @Test
    @DisplayName("Should set and get loan id")
    void testSetGetLoanId() {
        // Arrange
        CreateExtensionDTO dto = new CreateExtensionDTO();

        // Act
        dto.setLoanId(456);

        // Assert
        assertEquals(456, dto.getLoanId());
    }

    @Test
    @DisplayName("Should set and get username")
    void testSetGetUsername() {
        // Arrange
        CreateExtensionDTO dto = new CreateExtensionDTO();

        // Act
        dto.setUsername("janedoe");

        // Assert
        assertEquals("janedoe", dto.getUsername());
    }

    @Test
    @DisplayName("Should handle null username")
    void testNullUsername() {
        // Act
        CreateExtensionDTO dto = new CreateExtensionDTO(123, null);

        // Assert
        assertEquals(123, dto.getLoanId());
        assertNull(dto.getUsername());
    }

    @Test
    @DisplayName("Should handle empty username")
    void testEmptyUsername() {
        // Act
        CreateExtensionDTO dto = new CreateExtensionDTO(123, "");

        // Assert
        assertEquals(123, dto.getLoanId());
        assertEquals("", dto.getUsername());
    }

    @Test
    @DisplayName("Should handle zero loan id")
    void testZeroLoanId() {
        // Act
        CreateExtensionDTO dto = new CreateExtensionDTO(0, "user");

        // Assert
        assertEquals(0, dto.getLoanId());
    }

    @Test
    @DisplayName("Should handle negative loan id")
    void testNegativeLoanId() {
        // Act
        CreateExtensionDTO dto = new CreateExtensionDTO(-1, "user");

        // Assert
        assertEquals(-1, dto.getLoanId());
    }

    @Test
    @DisplayName("Should handle large loan id")
    void testLargeLoanId() {
        // Act
        CreateExtensionDTO dto = new CreateExtensionDTO(Integer.MAX_VALUE, "user");

        // Assert
        assertEquals(Integer.MAX_VALUE, dto.getLoanId());
    }

    @Test
    @DisplayName("Should handle special characters in username")
    void testSpecialCharactersInUsername() {
        // Act
        CreateExtensionDTO dto = new CreateExtensionDTO(123, "user@example.com");

        // Assert
        assertEquals("user@example.com", dto.getUsername());
    }

    @Test
    @DisplayName("Should update fields after creation")
    void testUpdateFieldsAfterCreation() {
        // Arrange
        CreateExtensionDTO dto = new CreateExtensionDTO(100, "user1");

        // Act
        dto.setLoanId(200);
        dto.setUsername("user2");

        // Assert
        assertEquals(200, dto.getLoanId());
        assertEquals("user2", dto.getUsername());
    }
}


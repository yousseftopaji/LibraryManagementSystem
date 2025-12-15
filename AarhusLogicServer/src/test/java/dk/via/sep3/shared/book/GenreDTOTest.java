package dk.via.sep3.shared.book;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GenreDTO
 * Tests DTO construction and field access
 */
class GenreDTOTest {

    @Test
    @DisplayName("Should create GenreDTO with constructor")
    void testConstructor() {
        // Act
        GenreDTO dto = new GenreDTO("Programming");

        // Assert
        assertNotNull(dto);
        assertEquals("Programming", dto.getName());
    }

    @Test
    @DisplayName("Should set and get name")
    void testSetGetName() {
        // Arrange
        GenreDTO dto = new GenreDTO("Fiction");

        // Act
        dto.setName("Science Fiction");

        // Assert
        assertEquals("Science Fiction", dto.getName());
    }

    @Test
    @DisplayName("Should handle null name")
    void testNullName() {
        // Act
        GenreDTO dto = new GenreDTO(null);

        // Assert
        assertNull(dto.getName());
    }

    @Test
    @DisplayName("Should handle empty name")
    void testEmptyName() {
        // Act
        GenreDTO dto = new GenreDTO("");

        // Assert
        assertEquals("", dto.getName());
    }

    @Test
    @DisplayName("Should handle special characters")
    void testSpecialCharacters() {
        // Act
        GenreDTO dto = new GenreDTO("Sci-Fi & Fantasy");

        // Assert
        assertEquals("Sci-Fi & Fantasy", dto.getName());
    }

    @Test
    @DisplayName("Should handle unicode characters")
    void testUnicodeCharacters() {
        // Act
        GenreDTO dto = new GenreDTO("Littérature Française");

        // Assert
        assertEquals("Littérature Française", dto.getName());
    }

    @Test
    @DisplayName("Should handle very long name")
    void testVeryLongName() {
        // Arrange
        String longName = "A".repeat(500);

        // Act
        GenreDTO dto = new GenreDTO(longName);

        // Assert
        assertEquals(longName, dto.getName());
    }

    @Test
    @DisplayName("Should update name")
    void testUpdateName() {
        // Arrange
        GenreDTO dto = new GenreDTO("Original");

        // Act
        dto.setName("Updated");

        // Assert
        assertEquals("Updated", dto.getName());
    }

    @Test
    @DisplayName("Should handle common genre names")
    void testCommonGenres() {
        GenreDTO programming = new GenreDTO("Programming");
        assertEquals("Programming", programming.getName());

        GenreDTO fiction = new GenreDTO("Fiction");
        assertEquals("Fiction", fiction.getName());

        GenreDTO biography = new GenreDTO("Biography");
        assertEquals("Biography", biography.getName());
    }

    @Test
    @DisplayName("Should preserve whitespace")
    void testWhitespacePreservation() {
        // Act
        GenreDTO dto = new GenreDTO("  Genre  ");

        // Assert
        assertEquals("  Genre  ", dto.getName());
    }
}


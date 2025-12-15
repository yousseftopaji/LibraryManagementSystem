package dk.via.sep3.model.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Genre domain class
 * Tests domain object construction and field access
 */
class GenreTest {

    @Test
    @DisplayName("Should create Genre with constructor")
    void testConstructor() {
        // Act
        Genre genre = new Genre("Programming");

        // Assert
        assertNotNull(genre);
        assertEquals("Programming", genre.getName());
    }

    @Test
    @DisplayName("Should set and get name")
    void testSetGetName() {
        // Arrange
        Genre genre = new Genre("Fiction");

        // Act
        genre.setName("Science Fiction");

        // Assert
        assertEquals("Science Fiction", genre.getName());
    }

    @Test
    @DisplayName("Should handle null name")
    void testNullName() {
        // Act
        Genre genre = new Genre(null);

        // Assert
        assertNull(genre.getName());
    }

    @Test
    @DisplayName("Should handle empty string name")
    void testEmptyStringName() {
        // Act
        Genre genre = new Genre("");

        // Assert
        assertEquals("", genre.getName());
    }

    @Test
    @DisplayName("Should handle special characters in name")
    void testSpecialCharactersInName() {
        // Act
        Genre genre = new Genre("Sci-Fi & Fantasy");

        // Assert
        assertEquals("Sci-Fi & Fantasy", genre.getName());
    }

    @Test
    @DisplayName("Should handle very long name")
    void testVeryLongName() {
        // Arrange
        String longName = "A".repeat(1000);

        // Act
        Genre genre = new Genre(longName);

        // Assert
        assertEquals(longName, genre.getName());
    }

    @Test
    @DisplayName("Should handle genre name with numbers")
    void testGenreNameWithNumbers() {
        // Act
        Genre genre = new Genre("20th Century Literature");

        // Assert
        assertEquals("20th Century Literature", genre.getName());
    }

    @Test
    @DisplayName("Should handle genre name with unicode characters")
    void testUnicodeCharacters() {
        // Act
        Genre genre = new Genre("Littérature Française");

        // Assert
        assertEquals("Littérature Française", genre.getName());
    }

    @Test
    @DisplayName("Should update genre name")
    void testUpdateGenreName() {
        // Arrange
        Genre genre = new Genre("Original");

        // Act
        genre.setName("Updated");

        // Assert
        assertEquals("Updated", genre.getName());
    }

    @Test
    @DisplayName("Should preserve whitespace in genre name")
    void testWhitespaceInName() {
        // Act
        Genre genre = new Genre("  Software   Engineering  ");

        // Assert
        assertEquals("  Software   Engineering  ", genre.getName());
    }

    @Test
    @DisplayName("Should handle single character name")
    void testSingleCharacterName() {
        // Act
        Genre genre = new Genre("A");

        // Assert
        assertEquals("A", genre.getName());
    }

    @Test
    @DisplayName("Should handle common genre names")
    void testCommonGenreNames() {
        // Programming
        Genre programming = new Genre("Programming");
        assertEquals("Programming", programming.getName());

        // Fiction
        Genre fiction = new Genre("Fiction");
        assertEquals("Fiction", fiction.getName());

        // Non-Fiction
        Genre nonFiction = new Genre("Non-Fiction");
        assertEquals("Non-Fiction", nonFiction.getName());

        // Science
        Genre science = new Genre("Science");
        assertEquals("Science", science.getName());

        // Biography
        Genre biography = new Genre("Biography");
        assertEquals("Biography", biography.getName());
    }
}


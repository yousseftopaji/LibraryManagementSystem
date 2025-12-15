package dk.via.sep3.shared.book;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BookDTO
 * Tests DTO construction and field access
 */
class BookDTOTest {

    private BookDTO bookDTO;
    private List<GenreDTO> genres;

    @BeforeEach
    void setUp() {
        bookDTO = new BookDTO();
        genres = new ArrayList<>();
        genres.add(new GenreDTO("Programming"));
        genres.add(new GenreDTO("Software Engineering"));
    }

    @Test
    @DisplayName("Should create BookDTO with no-arg constructor")
    void testNoArgConstructor() {
        // Act
        BookDTO dto = new BookDTO();

        // Assert
        assertNotNull(dto);
        assertEquals(0, dto.getId());
        assertNull(dto.getTitle());
        assertNull(dto.getAuthor());
        assertNull(dto.getIsbn());
        assertNull(dto.getState());
        assertNull(dto.getGenres());
    }

    @Test
    @DisplayName("Should create BookDTO with all args constructor")
    void testAllArgsConstructor() {
        // Act
        BookDTO dto = new BookDTO(
            1,
            "Clean Code",
            "Robert Martin",
            "978-0-123456-47-2",
            "Available",
            genres
        );

        // Assert
        assertEquals(1, dto.getId());
        assertEquals("Clean Code", dto.getTitle());
        assertEquals("Robert Martin", dto.getAuthor());
        assertEquals("978-0-123456-47-2", dto.getIsbn());
        assertEquals("Available", dto.getState());
        assertEquals(2, dto.getGenres().size());
        assertEquals("Programming", dto.getGenres().get(0).getName());
    }

    @Test
    @DisplayName("Should set and get id")
    void testSetGetId() {
        // Act
        bookDTO.setId(42);

        // Assert
        assertEquals(42, bookDTO.getId());
    }

    @Test
    @DisplayName("Should set and get title")
    void testSetGetTitle() {
        // Act
        bookDTO.setTitle("Design Patterns");

        // Assert
        assertEquals("Design Patterns", bookDTO.getTitle());
    }

    @Test
    @DisplayName("Should set and get author")
    void testSetGetAuthor() {
        // Act
        bookDTO.setAuthor("Gang of Four");

        // Assert
        assertEquals("Gang of Four", bookDTO.getAuthor());
    }

    @Test
    @DisplayName("Should set and get ISBN")
    void testSetGetIsbn() {
        // Act
        bookDTO.setIsbn("978-0-987654-32-1");

        // Assert
        assertEquals("978-0-987654-32-1", bookDTO.getIsbn());
    }

    @Test
    @DisplayName("Should set and get state")
    void testSetGetState() {
        // Act
        bookDTO.setState("Borrowed");

        // Assert
        assertEquals("Borrowed", bookDTO.getState());
    }

    @Test
    @DisplayName("Should set and get genres")
    void testSetGetGenres() {
        // Act
        bookDTO.setGenres(genres);

        // Assert
        assertNotNull(bookDTO.getGenres());
        assertEquals(2, bookDTO.getGenres().size());
        assertEquals("Programming", bookDTO.getGenres().get(0).getName());
        assertEquals("Software Engineering", bookDTO.getGenres().get(1).getName());
    }

    @Test
    @DisplayName("Should handle null values")
    void testNullValues() {
        // Act
        bookDTO.setTitle(null);
        bookDTO.setAuthor(null);
        bookDTO.setIsbn(null);
        bookDTO.setState(null);
        bookDTO.setGenres(null);

        // Assert
        assertNull(bookDTO.getTitle());
        assertNull(bookDTO.getAuthor());
        assertNull(bookDTO.getIsbn());
        assertNull(bookDTO.getState());
        assertNull(bookDTO.getGenres());
    }

    @Test
    @DisplayName("Should handle empty strings")
    void testEmptyStrings() {
        // Act
        bookDTO.setTitle("");
        bookDTO.setAuthor("");
        bookDTO.setIsbn("");
        bookDTO.setState("");

        // Assert
        assertEquals("", bookDTO.getTitle());
        assertEquals("", bookDTO.getAuthor());
        assertEquals("", bookDTO.getIsbn());
        assertEquals("", bookDTO.getState());
    }

    @Test
    @DisplayName("Should handle empty genre list")
    void testEmptyGenreList() {
        // Act
        bookDTO.setGenres(new ArrayList<>());

        // Assert
        assertNotNull(bookDTO.getGenres());
        assertTrue(bookDTO.getGenres().isEmpty());
    }

    @Test
    @DisplayName("Should handle single genre")
    void testSingleGenre() {
        // Arrange
        List<GenreDTO> singleGenre = Arrays.asList(new GenreDTO("Fiction"));

        // Act
        bookDTO.setGenres(singleGenre);

        // Assert
        assertEquals(1, bookDTO.getGenres().size());
        assertEquals("Fiction", bookDTO.getGenres().get(0).getName());
    }

    @Test
    @DisplayName("Should handle multiple genres")
    void testMultipleGenres() {
        // Arrange
        List<GenreDTO> multipleGenres = Arrays.asList(
            new GenreDTO("Programming"),
            new GenreDTO("Software Engineering"),
            new GenreDTO("Best Practices"),
            new GenreDTO("Refactoring")
        );

        // Act
        bookDTO.setGenres(multipleGenres);

        // Assert
        assertEquals(4, bookDTO.getGenres().size());
        assertEquals("Programming", bookDTO.getGenres().get(0).getName());
        assertEquals("Refactoring", bookDTO.getGenres().get(3).getName());
    }

    @Test
    @DisplayName("Should handle different states")
    void testDifferentStates() {
        // Available
        bookDTO.setState("Available");
        assertEquals("Available", bookDTO.getState());

        // Borrowed
        bookDTO.setState("Borrowed");
        assertEquals("Borrowed", bookDTO.getState());

        // Reserved
        bookDTO.setState("Reserved");
        assertEquals("Reserved", bookDTO.getState());
    }

    @Test
    @DisplayName("Should handle special characters in title")
    void testSpecialCharactersInTitle() {
        // Act
        bookDTO.setTitle("C++ Programming: Édition Spéciale");

        // Assert
        assertEquals("C++ Programming: Édition Spéciale", bookDTO.getTitle());
    }

    @Test
    @DisplayName("Should handle special characters in author")
    void testSpecialCharactersInAuthor() {
        // Act
        bookDTO.setAuthor("José María García-López");

        // Assert
        assertEquals("José María García-López", bookDTO.getAuthor());
    }

    @Test
    @DisplayName("Should handle very long title")
    void testVeryLongTitle() {
        // Arrange
        String longTitle = "This is a very long book title that goes on and on ".repeat(10);

        // Act
        bookDTO.setTitle(longTitle);

        // Assert
        assertEquals(longTitle, bookDTO.getTitle());
    }

    @Test
    @DisplayName("Should handle negative id")
    void testNegativeId() {
        // Act
        bookDTO.setId(-1);

        // Assert
        assertEquals(-1, bookDTO.getId());
    }

    @Test
    @DisplayName("Should handle zero id")
    void testZeroId() {
        // Act
        bookDTO.setId(0);

        // Assert
        assertEquals(0, bookDTO.getId());
    }

    @Test
    @DisplayName("Should handle large id")
    void testLargeId() {
        // Act
        bookDTO.setId(Integer.MAX_VALUE);

        // Assert
        assertEquals(Integer.MAX_VALUE, bookDTO.getId());
    }

    @Test
    @DisplayName("Should preserve all fields in constructor")
    void testConstructor_PreservesAllFields() {
        // Act
        BookDTO dto = new BookDTO(
            123,
            "Test Book",
            "Test Author",
            "ISBN-123",
            "Available",
            genres
        );

        // Assert
        assertEquals(123, dto.getId());
        assertEquals("Test Book", dto.getTitle());
        assertEquals("Test Author", dto.getAuthor());
        assertEquals("ISBN-123", dto.getIsbn());
        assertEquals("Available", dto.getState());
        assertEquals(genres, dto.getGenres());
    }

    @Test
    @DisplayName("Should handle ISBN with different formats")
    void testDifferentISBNFormats() {
        // ISBN-10
        bookDTO.setIsbn("0-123456-47-2");
        assertEquals("0-123456-47-2", bookDTO.getIsbn());

        // ISBN-13
        bookDTO.setIsbn("978-0-123456-47-2");
        assertEquals("978-0-123456-47-2", bookDTO.getIsbn());

        // Without hyphens
        bookDTO.setIsbn("9780123456472");
        assertEquals("9780123456472", bookDTO.getIsbn());
    }

    @Test
    @DisplayName("Should handle modifying genre list")
    void testModifyGenreList() {
        // Arrange
        List<GenreDTO> modifiableGenres = new ArrayList<>();
        modifiableGenres.add(new GenreDTO("Original"));
        bookDTO.setGenres(modifiableGenres);

        // Act - Add new genre
        bookDTO.getGenres().add(new GenreDTO("Added"));

        // Assert
        assertEquals(2, bookDTO.getGenres().size());
        assertEquals("Original", bookDTO.getGenres().get(0).getName());
        assertEquals("Added", bookDTO.getGenres().get(1).getName());
    }
}


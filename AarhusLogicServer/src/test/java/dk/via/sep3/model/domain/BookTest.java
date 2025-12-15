package dk.via.sep3.model.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Book domain class
 * Tests domain object construction and business methods
 */
class BookTest {

    private Book book;
    private List<Genre> genres;

    @BeforeEach
    void setUp() {
        genres = new ArrayList<>();
        genres.add(new Genre("Programming"));
        genres.add(new Genre("Software Engineering"));
        book = new Book();
    }

    @Test
    @DisplayName("Should create Book with no-arg constructor")
    void testNoArgConstructor() {
        // Act
        Book b = new Book();

        // Assert
        assertNotNull(b);
        assertEquals(0, b.getId());
        assertNull(b.getIsbn());
        assertNull(b.getTitle());
        assertNull(b.getAuthor());
        assertNull(b.getState());
        assertNull(b.getGenres());
    }

    @Test
    @DisplayName("Should create Book with constructor without id")
    void testConstructorWithoutId() {
        // Act
        Book b = new Book(
            "978-0-123456-47-2",
            "Clean Code",
            "Robert Martin",
            State.AVAILABLE,
            genres
        );

        // Assert
        assertEquals("978-0-123456-47-2", b.getIsbn());
        assertEquals("Clean Code", b.getTitle());
        assertEquals("Robert Martin", b.getAuthor());
        assertEquals(State.AVAILABLE, b.getState());
        assertEquals(2, b.getGenres().size());
    }

    @Test
    @DisplayName("Should create Book with all args constructor")
    void testAllArgsConstructor() {
        // Act
        Book b = new Book(
            1,
            "978-0-123456-47-2",
            "Clean Code",
            "Robert Martin",
            State.AVAILABLE,
            genres
        );

        // Assert
        assertEquals(1, b.getId());
        assertEquals("978-0-123456-47-2", b.getIsbn());
        assertEquals("Clean Code", b.getTitle());
        assertEquals("Robert Martin", b.getAuthor());
        assertEquals(State.AVAILABLE, b.getState());
        assertEquals(2, b.getGenres().size());
    }

    @Test
    @DisplayName("Should set and get id")
    void testSetGetId() {
        // Act
        book.setId(42);

        // Assert
        assertEquals(42, book.getId());
    }

    @Test
    @DisplayName("Should set and get ISBN")
    void testSetGetIsbn() {
        // Act
        book.setIsbn("978-0-987654-32-1");

        // Assert
        assertEquals("978-0-987654-32-1", book.getIsbn());
    }

    @Test
    @DisplayName("Should set and get title")
    void testSetGetTitle() {
        // Act
        book.setTitle("Design Patterns");

        // Assert
        assertEquals("Design Patterns", book.getTitle());
    }

    @Test
    @DisplayName("Should set and get author")
    void testSetGetAuthor() {
        // Act
        book.setAuthor("Gang of Four");

        // Assert
        assertEquals("Gang of Four", book.getAuthor());
    }

    @Test
    @DisplayName("Should set and get state")
    void testSetGetState() {
        // Act
        book.setState(State.BORROWED);

        // Assert
        assertEquals(State.BORROWED, book.getState());
    }

    @Test
    @DisplayName("Should set and get genres")
    void testSetGetGenres() {
        // Act
        book.setGenres(genres);

        // Assert
        assertEquals(2, book.getGenres().size());
        assertEquals("Programming", book.getGenres().get(0).getName());
    }

    @Test
    @DisplayName("Should add genre to book")
    void testAddGenre() {
        // Arrange
        book.setGenres(new ArrayList<>());

        // Act
        book.addGenre(new Genre("Fiction"));
        book.addGenre(new Genre("Science"));

        // Assert
        assertEquals(2, book.getGenres().size());
        assertEquals("Fiction", book.getGenres().get(0).getName());
        assertEquals("Science", book.getGenres().get(1).getName());
    }

    @Test
    @DisplayName("Should return true when book is available")
    void testIsAvailable_True() {
        // Arrange
        book.setState(State.AVAILABLE);

        // Act & Assert
        assertTrue(book.isAvailable());
    }

    @Test
    @DisplayName("Should return false when book is borrowed")
    void testIsAvailable_False_Borrowed() {
        // Arrange
        book.setState(State.BORROWED);

        // Act & Assert
        assertFalse(book.isAvailable());
    }

    @Test
    @DisplayName("Should return false when book is reserved")
    void testIsAvailable_False_Reserved() {
        // Arrange
        book.setState(State.RESERVED);

        // Act & Assert
        assertFalse(book.isAvailable());
    }

    @Test
    @DisplayName("Should handle null state in isAvailable")
    void testIsAvailable_NullState() {
        // Arrange
        book.setState(null);

        // Act & Assert
        assertFalse(book.isAvailable());
    }

    @Test
    @DisplayName("Should handle all book states")
    void testAllStates() {
        // AVAILABLE
        book.setState(State.AVAILABLE);
        assertEquals(State.AVAILABLE, book.getState());
        assertTrue(book.isAvailable());

        // BORROWED
        book.setState(State.BORROWED);
        assertEquals(State.BORROWED, book.getState());
        assertFalse(book.isAvailable());

        // RESERVED
        book.setState(State.RESERVED);
        assertEquals(State.RESERVED, book.getState());
        assertFalse(book.isAvailable());
    }

    @Test
    @DisplayName("Should handle empty genre list")
    void testEmptyGenreList() {
        // Act
        book.setGenres(new ArrayList<>());

        // Assert
        assertNotNull(book.getGenres());
        assertTrue(book.getGenres().isEmpty());
    }

    @Test
    @DisplayName("Should handle single genre")
    void testSingleGenre() {
        // Act
        book.setGenres(Arrays.asList(new Genre("Fiction")));

        // Assert
        assertEquals(1, book.getGenres().size());
    }

    @Test
    @DisplayName("Should handle multiple genres")
    void testMultipleGenres() {
        // Arrange
        List<Genre> multiGenres = Arrays.asList(
            new Genre("Programming"),
            new Genre("Software Engineering"),
            new Genre("Best Practices"),
            new Genre("Refactoring")
        );

        // Act
        book.setGenres(multiGenres);

        // Assert
        assertEquals(4, book.getGenres().size());
    }

    @Test
    @DisplayName("Should add genre to existing list")
    void testAddGenreToExistingList() {
        // Arrange
        book.setGenres(genres);
        int originalSize = book.getGenres().size();

        // Act
        book.addGenre(new Genre("New Genre"));

        // Assert
        assertEquals(originalSize + 1, book.getGenres().size());
        assertEquals("New Genre", book.getGenres().get(originalSize).getName());
    }

    @Test
    @DisplayName("Should handle null values")
    void testNullValues() {
        // Act
        book.setIsbn(null);
        book.setTitle(null);
        book.setAuthor(null);
        book.setState(null);
        book.setGenres(null);

        // Assert
        assertNull(book.getIsbn());
        assertNull(book.getTitle());
        assertNull(book.getAuthor());
        assertNull(book.getState());
        assertNull(book.getGenres());
    }

    @Test
    @DisplayName("Should handle special characters")
    void testSpecialCharacters() {
        // Act
        book.setTitle("C++ Programming: Édition Spéciale");
        book.setAuthor("José María García-López");

        // Assert
        assertEquals("C++ Programming: Édition Spéciale", book.getTitle());
        assertEquals("José María García-López", book.getAuthor());
    }

    @Test
    @DisplayName("Should preserve all fields")
    void testPreserveAllFields() {
        // Arrange
        Book b = new Book(
            99,
            "ISBN-TEST",
            "Test Title",
            "Test Author",
            State.AVAILABLE,
            genres
        );

        // Assert
        assertEquals(99, b.getId());
        assertEquals("ISBN-TEST", b.getIsbn());
        assertEquals("Test Title", b.getTitle());
        assertEquals("Test Author", b.getAuthor());
        assertEquals(State.AVAILABLE, b.getState());
        assertEquals(2, b.getGenres().size());
    }

    @Test
    @DisplayName("Should handle negative id")
    void testNegativeId() {
        // Act
        book.setId(-1);

        // Assert
        assertEquals(-1, book.getId());
    }

    @Test
    @DisplayName("Should handle large id")
    void testLargeId() {
        // Act
        book.setId(Integer.MAX_VALUE);

        // Assert
        assertEquals(Integer.MAX_VALUE, book.getId());
    }
}


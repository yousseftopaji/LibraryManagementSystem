package dk.via.sep3.application.services.books;

import dk.via.sep3.exceptionHandler.ResourceNotFoundException;
import dk.via.sep3.grpcConnection.bookGrpcService.BookGrpcService;
import dk.via.sep3.application.domain.Book;
import dk.via.sep3.application.domain.Genre;
import dk.via.sep3.application.domain.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BookServiceImpl
 * Tests book retrieval and filtering logic
 */
@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookGrpcService bookGrpcService;

    @InjectMocks
    private BookServiceImpl bookService;

    private List<Book> sampleBooks;
    private Book availableBook1;
    private Book availableBook2;
    private Book borrowedBook;

    @BeforeEach
    void setUp() {
        // Create sample books with different states
        availableBook1 = new Book(1, "978-0-123456-47-2", "Clean Code", "Robert Martin",
                                   State.AVAILABLE, Arrays.asList(new Genre("Programming")));

        availableBook2 = new Book(2, "978-0-987654-32-1", "Design Patterns", "Gang of Four",
                                   State.AVAILABLE, Arrays.asList(new Genre("Software Engineering")));

        borrowedBook = new Book(3, "978-0-123456-47-2", "Clean Code", "Robert Martin",
                                State.BORROWED, Arrays.asList(new Genre("Programming")));

        sampleBooks = new ArrayList<>(Arrays.asList(availableBook1, availableBook2, borrowedBook));
    }

    // ========== getAllBooks Tests ==========

    @Test
    @DisplayName("Should return unique books by ISBN")
    void testGetAllBooks_ReturnsUniqueBooks() {
        // Arrange
        when(bookGrpcService.getAllBooks()).thenReturn(sampleBooks);

        // Act
        List<Book> result = bookService.getAllBooks();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // Only 2 unique ISBNs
        verify(bookGrpcService, times(1)).getAllBooks();
    }

    @Test
    @DisplayName("Should return empty list when no books exist")
    void testGetAllBooks_EmptyList() {
        // Arrange
        when(bookGrpcService.getAllBooks()).thenReturn(new ArrayList<>());

        // Act
        List<Book> result = bookService.getAllBooks();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookGrpcService, times(1)).getAllBooks();
    }

    @Test
    @DisplayName("Should filter duplicate ISBNs keeping first occurrence")
    void testGetAllBooks_FiltersDuplicates() {
        // Arrange - Create books with same ISBN
        Book duplicate1 = new Book(1, "ISBN-123", "Book A", "Author A", State.AVAILABLE, new ArrayList<>());
        Book duplicate2 = new Book(2, "ISBN-123", "Book A", "Author A", State.BORROWED, new ArrayList<>());
        Book unique = new Book(3, "ISBN-456", "Book B", "Author B", State.AVAILABLE, new ArrayList<>());

        when(bookGrpcService.getAllBooks()).thenReturn(Arrays.asList(duplicate1, duplicate2, unique));

        // Act
        List<Book> result = bookService.getAllBooks();

        // Assert
        assertEquals(2, result.size());
        assertEquals("ISBN-123", result.get(0).getIsbn());
        assertEquals(1, result.get(0).getId()); // First occurrence kept
        verify(bookGrpcService, times(1)).getAllBooks();
    }

    @Test
    @DisplayName("Should preserve insertion order for unique books")
    void testGetAllBooks_PreservesOrder() {
        // Arrange
        when(bookGrpcService.getAllBooks()).thenReturn(sampleBooks);

        // Act
        List<Book> result = bookService.getAllBooks();

        // Assert
        assertEquals("978-0-123456-47-2", result.get(0).getIsbn()); // Clean Code first
        assertEquals("978-0-987654-32-1", result.get(1).getIsbn()); // Design Patterns second
    }

    // ========== getBookByIsbn Tests ==========

    @Test
    @DisplayName("Should return available book when available copy exists")
    void testGetBookByIsbn_AvailableBookExists() {
        // Arrange
        String isbn = "978-0-123456-47-2";
        when(bookGrpcService.getBooksByIsbn(isbn))
            .thenReturn(Arrays.asList(availableBook1, borrowedBook));

        // Act
        Book result = bookService.getBookByIsbn(isbn);

        // Assert
        assertNotNull(result);
        assertEquals(State.AVAILABLE, result.getState());
        assertEquals(isbn, result.getIsbn());
        verify(bookGrpcService, times(1)).getBooksByIsbn(isbn);
    }

    @Test
    @DisplayName("Should return first book when no available copy exists")
    void testGetBookByIsbn_NoAvailableCopy() {
        // Arrange
        String isbn = "978-0-123456-47-2";
        Book borrowedBook2 = new Book(4, isbn, "Clean Code", "Robert Martin",
                                      State.BORROWED, new ArrayList<>());
        when(bookGrpcService.getBooksByIsbn(isbn))
            .thenReturn(Arrays.asList(borrowedBook, borrowedBook2));

        // Act
        Book result = bookService.getBookByIsbn(isbn);

        // Assert
        assertNotNull(result);
        assertEquals(borrowedBook, result); // Returns first book
        verify(bookGrpcService, times(1)).getBooksByIsbn(isbn);
    }

    @Test
    @DisplayName("Should throw exception when ISBN not found")
    void testGetBookByIsbn_NotFound() {
        // Arrange
        String isbn = "978-0-000000-00-0";
        when(bookGrpcService.getBooksByIsbn(isbn)).thenReturn(new ArrayList<>());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> bookService.getBookByIsbn(isbn)
        );

        assertEquals("Book with ISBN " + isbn + " not found", exception.getMessage());
        verify(bookGrpcService, times(1)).getBooksByIsbn(isbn);
    }

    @Test
    @DisplayName("Should prefer available book over borrowed book")
    void testGetBookByIsbn_PrefersAvailable() {
        // Arrange
        String isbn = "978-0-123456-47-2";
        // Place borrowed book first, available book second
        when(bookGrpcService.getBooksByIsbn(isbn))
            .thenReturn(Arrays.asList(borrowedBook, availableBook1));

        // Act
        Book result = bookService.getBookByIsbn(isbn);

        // Assert
        assertEquals(State.AVAILABLE, result.getState());
        assertEquals(availableBook1.getId(), result.getId());
    }

    @Test
    @DisplayName("Should handle case-insensitive state comparison")
    void testGetBookByIsbn_CaseInsensitiveState() {
        // Arrange
        String isbn = "978-0-123456-47-2";
        Book availableWithLowerCase = new Book(5, isbn, "Clean Code", "Robert Martin",
                                                State.AVAILABLE, new ArrayList<>());
        when(bookGrpcService.getBooksByIsbn(isbn))
            .thenReturn(Arrays.asList(availableWithLowerCase));

        // Act
        Book result = bookService.getBookByIsbn(isbn);

        // Assert
        assertNotNull(result);
        assertEquals(availableWithLowerCase, result);
    }

    @Test
    @DisplayName("Should handle single book result")
    void testGetBookByIsbn_SingleBook() {
        // Arrange
        String isbn = "978-0-987654-32-1";
        when(bookGrpcService.getBooksByIsbn(isbn))
            .thenReturn(Arrays.asList(availableBook2));

        // Act
        Book result = bookService.getBookByIsbn(isbn);

        // Assert
        assertNotNull(result);
        assertEquals(availableBook2, result);
        verify(bookGrpcService, times(1)).getBooksByIsbn(isbn);
    }

    @Test
    @DisplayName("Should handle gRPC service exception")
    void testGetBookByIsbn_GrpcException() {
        // Arrange
        String isbn = "978-0-123456-47-2";
        when(bookGrpcService.getBooksByIsbn(isbn))
            .thenThrow(new RuntimeException("gRPC connection failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> bookService.getBookByIsbn(isbn)
        );

        assertEquals("gRPC connection failed", exception.getMessage());
        verify(bookGrpcService, times(1)).getBooksByIsbn(isbn);
    }

    @Test
    @DisplayName("Should return book with all properties intact")
    void testGetBookByIsbn_PreservesAllProperties() {
        // Arrange
        String isbn = "978-0-123456-47-2";
        when(bookGrpcService.getBooksByIsbn(isbn))
            .thenReturn(Arrays.asList(availableBook1));

        // Act
        Book result = bookService.getBookByIsbn(isbn);

        // Assert
        assertEquals(availableBook1.getId(), result.getId());
        assertEquals(availableBook1.getIsbn(), result.getIsbn());
        assertEquals(availableBook1.getTitle(), result.getTitle());
        assertEquals(availableBook1.getAuthor(), result.getAuthor());
        assertEquals(availableBook1.getState(), result.getState());
        assertNotNull(result.getGenres());
        assertEquals(1, result.getGenres().size());
    }

    @Test
    @DisplayName("Should handle books with multiple genres")
    void testGetAllBooks_MultipleGenres() {
        // Arrange
        List<Genre> multipleGenres = Arrays.asList(
            new Genre("Programming"),
            new Genre("Software Engineering"),
            new Genre("Best Practices")
        );
        Book bookWithGenres = new Book(10, "ISBN-MULTI", "Multi Genre Book", "Author",
                                       State.AVAILABLE, multipleGenres);
        when(bookGrpcService.getAllBooks()).thenReturn(Arrays.asList(bookWithGenres));

        // Act
        List<Book> result = bookService.getAllBooks();

        // Assert
        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getGenres().size());
    }

    @Test
    @DisplayName("Should handle books without genres")
    void testGetAllBooks_NoGenres() {
        // Arrange
        Book bookWithoutGenres = new Book(11, "ISBN-NONE", "No Genre Book", "Author",
                                          State.AVAILABLE, new ArrayList<>());
        when(bookGrpcService.getAllBooks()).thenReturn(Arrays.asList(bookWithoutGenres));

        // Act
        List<Book> result = bookService.getAllBooks();

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).getGenres().isEmpty());
    }
}


package dk.via.sep3.controller;

import dk.via.sep3.application.services.books.BookService;
import dk.via.sep3.application.domain.Book;
import dk.via.sep3.application.domain.Genre;
import dk.via.sep3.application.domain.State;
import dk.via.sep3.DTOs.book.BookDTO;
import dk.via.sep3.DTOs.book.GenreDTO;
import dk.via.sep3.mapper.bookMapper.BookMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BooksController
 * Tests book retrieval functionality with various scenarios
 */
@ExtendWith(MockitoExtension.class)
class BooksControllerTest {

    @Mock
    private BookService bookService;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BooksController booksController;

    private Book book1;
    private Book book2;
    private BookDTO bookDTO1;
    private BookDTO bookDTO2;

    @BeforeEach
    void setUp() {
        // Setup genres
        Genre genre1 = new Genre("Fiction");
        Genre genre2 = new Genre("Mystery");
        List<Genre> genres = Arrays.asList(genre1, genre2);

        GenreDTO genreDTO1 = new GenreDTO("Fiction");
        GenreDTO genreDTO2 = new GenreDTO("Mystery");
        List<GenreDTO> genreDTOs = Arrays.asList(genreDTO1, genreDTO2);

        // Setup book 1
        book1 = new Book();
        book1.setId(1);
        book1.setTitle("Test Book 1");
        book1.setAuthor("Author 1");
        book1.setIsbn("1234567890");
        book1.setState(State.AVAILABLE);
        book1.setGenres(genres);

        // Setup book 2
        book2 = new Book();
        book2.setId(2);
        book2.setTitle("Test Book 2");
        book2.setAuthor("Author 2");
        book2.setIsbn("0987654321");
        book2.setState(State.BORROWED);
        book2.setGenres(genres);

        // Setup bookDTO 1
        bookDTO1 = new BookDTO();
        bookDTO1.setId(1);
        bookDTO1.setTitle("Test Book 1");
        bookDTO1.setAuthor("Author 1");
        bookDTO1.setIsbn("1234567890");
        bookDTO1.setState("AVAILABLE");
        bookDTO1.setGenres(genreDTOs);

        // Setup bookDTO 2
        bookDTO2 = new BookDTO();
        bookDTO2.setId(2);
        bookDTO2.setTitle("Test Book 2");
        bookDTO2.setAuthor("Author 2");
        bookDTO2.setIsbn("0987654321");
        bookDTO2.setState("BORROWED");
        bookDTO2.setGenres(genreDTOs);
    }

    @Test
    @DisplayName("Should return all books when getAllBooks is called")
    void testGetAllBooks_Success() {
        // Arrange
        List<Book> books = Arrays.asList(book1, book2);
        when(bookService.getAllBooks()).thenReturn(books);
        when(bookMapper.toDto(book1)).thenReturn(bookDTO1);
        when(bookMapper.toDto(book2)).thenReturn(bookDTO2);

        // Act
        ResponseEntity<List<BookDTO>> response = booksController.getAllBooks();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Test Book 1", response.getBody().get(0).getTitle());
        assertEquals("Test Book 2", response.getBody().get(1).getTitle());

        // Verify interactions
        verify(bookService, times(1)).getAllBooks();
        verify(bookMapper, times(1)).toDto(book1);
        verify(bookMapper, times(1)).toDto(book2);
    }

    @Test
    @DisplayName("Should return empty list when no books exist")
    void testGetAllBooks_EmptyList() {
        // Arrange
        when(bookService.getAllBooks()).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<List<BookDTO>> response = booksController.getAllBooks();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());

        // Verify interactions
        verify(bookService, times(1)).getAllBooks();
        verify(bookMapper, never()).toDto(any(Book.class));
    }

    @Test
    @DisplayName("Should return book when valid ISBN is provided")
    void testGetBooksByIsbn_Success() {
        // Arrange
        String isbn = "1234567890";
        when(bookService.getBookByIsbn(isbn)).thenReturn(book1);
        when(bookMapper.toDto(book1)).thenReturn(bookDTO1);

        // Act
        ResponseEntity<BookDTO> response = booksController.getBooksByIsbn(isbn);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Book 1", response.getBody().getTitle());
        assertEquals(isbn, response.getBody().getIsbn());
        assertEquals("AVAILABLE", response.getBody().getState());

        // Verify interactions
        verify(bookService, times(1)).getBookByIsbn(isbn);
        verify(bookMapper, times(1)).toDto(book1);
    }

    @Test
    @DisplayName("Should throw exception when book with ISBN not found")
    void testGetBooksByIsbn_NotFound() {
        // Arrange
        String isbn = "9999999999";
        when(bookService.getBookByIsbn(isbn)).thenThrow(new IllegalArgumentException("Book not found with ISBN: " + isbn));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> booksController.getBooksByIsbn(isbn));

        // Verify interactions
        verify(bookService, times(1)).getBookByIsbn(isbn);
        verify(bookMapper, never()).toDto(any(Book.class));
    }

    @Test
    @DisplayName("Should map all book fields correctly")
    void testGetBooksByIsbn_FieldMapping() {
        // Arrange
        String isbn = "1234567890";
        when(bookService.getBookByIsbn(isbn)).thenReturn(book1);
        when(bookMapper.toDto(book1)).thenReturn(bookDTO1);

        // Act
        ResponseEntity<BookDTO> response = booksController.getBooksByIsbn(isbn);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());
        assertEquals("Test Book 1", response.getBody().getTitle());
        assertEquals("Author 1", response.getBody().getAuthor());
        assertEquals("1234567890", response.getBody().getIsbn());
        assertEquals("AVAILABLE", response.getBody().getState());
        assertEquals(2, response.getBody().getGenres().size());

        // Verify interactions
        verify(bookService, times(1)).getBookByIsbn(isbn);
        verify(bookMapper, times(1)).toDto(book1);
    }

    @Test
    @DisplayName("Should handle null ISBN gracefully")
    void testGetBooksByIsbn_NullIsbn() {
        // Arrange
        when(bookService.getBookByIsbn(null)).thenThrow(new IllegalArgumentException("ISBN cannot be null"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> booksController.getBooksByIsbn(null));

        // Verify interactions
        verify(bookService, times(1)).getBookByIsbn(null);
        verify(bookMapper, never()).toDto(any(Book.class));
    }

    @Test
    @DisplayName("Should handle empty ISBN gracefully")
    void testGetBooksByIsbn_EmptyIsbn() {
        // Arrange
        String emptyIsbn = "";
        when(bookService.getBookByIsbn(emptyIsbn)).thenThrow(new IllegalArgumentException("ISBN cannot be empty"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> booksController.getBooksByIsbn(emptyIsbn));

        // Verify interactions
        verify(bookService, times(1)).getBookByIsbn(emptyIsbn);
        verify(bookMapper, never()).toDto(any(Book.class));
    }
}


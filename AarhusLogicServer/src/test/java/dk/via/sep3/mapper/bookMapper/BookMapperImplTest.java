package dk.via.sep3.mapper.bookMapper;

import dk.via.sep3.DTOBook;
import dk.via.sep3.DTOGenre;
import dk.via.sep3.application.domain.Book;
import dk.via.sep3.application.domain.Genre;
import dk.via.sep3.application.domain.State;
import dk.via.sep3.DTOs.book.BookDTO;
import dk.via.sep3.DTOs.book.GenreDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookMapperImplTest {

    private BookMapperImpl bookMapper;

    @BeforeEach
    void setUp() {
        bookMapper = new BookMapperImpl();
    }

    @Test
    @DisplayName("Should map Book domain to BookDTO")
    void testToDto() {
        // Arrange
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre("Fiction"));
        Book book = new Book(1, "123456", "Test Book", "Test Author", State.AVAILABLE, genres);

        // Act
        BookDTO result = bookMapper.toDto(book);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("123456", result.getIsbn());
        assertEquals("Test Book", result.getTitle());
        assertEquals("Test Author", result.getAuthor());
        assertEquals("AVAILABLE", result.getState());
        assertEquals(1, result.getGenres().size());
        assertEquals("Fiction", result.getGenres().get(0).getName());
    }

    @Test
    @DisplayName("Should map BookDTO to Book domain")
    void testToDomain_FromBookDTO() {
        // Arrange
        List<GenreDTO> genreDTOs = new ArrayList<>();
        genreDTOs.add(new GenreDTO("Fiction"));
        BookDTO bookDTO = new BookDTO(1, "Test Book", "Test Author", "123456", "AVAILABLE", genreDTOs);

        // Act
        Book result = bookMapper.toDomain(bookDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("123456", result.getIsbn());
        assertEquals("Test Book", result.getTitle());
        assertEquals("Test Author", result.getAuthor());
        assertEquals(State.AVAILABLE, result.getState());
        assertEquals(1, result.getGenres().size());
        assertEquals("Fiction", result.getGenres().get(0).getName());
    }

    @Test
    @DisplayName("Should map DTOBook proto to Book domain")
    void testToDomain_FromDTOBook() {
        // Arrange
        DTOGenre dtoGenre = DTOGenre.newBuilder().setName("Fiction").build();
        DTOBook dtoBook = DTOBook.newBuilder()
            .setId(1)
            .setIsbn("123456")
            .setTitle("Test Book")
            .setAuthor("Test Author")
            .setState("AVAILABLE")
            .addGenres(dtoGenre)
            .build();

        // Act
        Book result = bookMapper.toDomain(dtoBook);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("123456", result.getIsbn());
        assertEquals("Test Book", result.getTitle());
        assertEquals("Test Author", result.getAuthor());
        assertEquals(State.AVAILABLE, result.getState());
        assertEquals(1, result.getGenres().size());
        assertEquals("Fiction", result.getGenres().get(0).getName());
    }

    @Test
    @DisplayName("Should map Book domain to DTOBook proto")
    void testToProto() {
        // Arrange
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre("Fiction"));
        Book book = new Book(1, "123456", "Test Book", "Test Author", State.AVAILABLE, genres);

        // Act
        DTOBook result = bookMapper.toProto(book);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("123456", result.getIsbn());
        assertEquals("Test Book", result.getTitle());
        assertEquals("Test Author", result.getAuthor());
        assertEquals("AVAILABLE", result.getState());
        assertEquals(1, result.getGenresCount());
        assertEquals("Fiction", result.getGenres(0).getName());
    }

    @Test
    @DisplayName("Should handle book with no genres")
    void testToDto_NoGenres() {
        // Arrange
        Book book = new Book(1, "123456", "Test Book", "Test Author", State.AVAILABLE, new ArrayList<>());

        // Act
        BookDTO result = bookMapper.toDto(book);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getGenres());
        assertTrue(result.getGenres().isEmpty());
    }

    @Test
    @DisplayName("Should handle book with multiple genres")
    void testToDto_MultipleGenres() {
        // Arrange
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre("Fiction"));
        genres.add(new Genre("Adventure"));
        genres.add(new Genre("Mystery"));
        Book book = new Book(1, "123456", "Test Book", "Test Author", State.AVAILABLE, genres);

        // Act
        BookDTO result = bookMapper.toDto(book);

        // Assert
        assertEquals(3, result.getGenres().size());
        assertEquals("Fiction", result.getGenres().get(0).getName());
        assertEquals("Adventure", result.getGenres().get(1).getName());
        assertEquals("Mystery", result.getGenres().get(2).getName());
    }

    @Test
    @DisplayName("Should handle different book states")
    void testToDto_DifferentStates() {
        // Test AVAILABLE
        Book availableBook = new Book(1, "123", "Book", "Author", State.AVAILABLE, new ArrayList<>());
        assertEquals("AVAILABLE", bookMapper.toDto(availableBook).getState());

        // Test BORROWED
        Book borrowedBook = new Book(2, "456", "Book", "Author", State.BORROWED, new ArrayList<>());
        assertEquals("BORROWED", bookMapper.toDto(borrowedBook).getState());

        // Test RESERVED
        Book reservedBook = new Book(3, "789", "Book", "Author", State.RESERVED, new ArrayList<>());
        assertEquals("RESERVED", bookMapper.toDto(reservedBook).getState());
    }

    @Test
    @DisplayName("Should preserve all fields when mapping to domain")
    void testToDomain_PreservesAllFields() {
        // Arrange
        List<GenreDTO> genreDTOs = new ArrayList<>();
        genreDTOs.add(new GenreDTO("Fiction"));
        BookDTO bookDTO = new BookDTO(99, "Complex Book", "Famous Author", "987654321", "BORROWED", genreDTOs);

        // Act
        Book result = bookMapper.toDomain(bookDTO);

        // Assert
        assertEquals(99, result.getId());
        assertEquals("987654321", result.getIsbn());
        assertEquals("Complex Book", result.getTitle());
        assertEquals("Famous Author", result.getAuthor());
        assertEquals(State.BORROWED, result.getState());
        assertEquals(1, result.getGenres().size());
    }

    @Test
    @DisplayName("Should map case-insensitive state strings")
    void testToDomain_CaseInsensitiveState() {
        // Arrange
        BookDTO lowerCaseDTO = new BookDTO(1, "Book", "Author", "123", "available", new ArrayList<>());

        // Act
        Book result = bookMapper.toDomain(lowerCaseDTO);

        // Assert
        assertEquals(State.AVAILABLE, result.getState());
    }

    @Test
    @DisplayName("Should handle genre mapping correctly")
    void testGenreMapping() {
        // Arrange
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre("Science Fiction"));
        genres.add(new Genre("Fantasy"));
        Book book = new Book(1, "123", "Book", "Author", State.AVAILABLE, genres);

        // Act
        DTOBook proto = bookMapper.toProto(book);
        Book backToDomain = bookMapper.toDomain(proto);

        // Assert
        assertEquals(2, backToDomain.getGenres().size());
        assertEquals("Science Fiction", backToDomain.getGenres().get(0).getName());
        assertEquals("Fantasy", backToDomain.getGenres().get(1).getName());
    }

    @Test
    @DisplayName("Should round-trip conversion maintain data integrity")
    void testRoundTripConversion() {
        // Arrange
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre("Fiction"));
        Book original = new Book(1, "123456", "Test Book", "Test Author", State.AVAILABLE, genres);

        // Act
        BookDTO dto = bookMapper.toDto(original);
        Book converted = bookMapper.toDomain(dto);

        // Assert
        assertEquals(original.getId(), converted.getId());
        assertEquals(original.getIsbn(), converted.getIsbn());
        assertEquals(original.getTitle(), converted.getTitle());
        assertEquals(original.getAuthor(), converted.getAuthor());
        assertEquals(original.getState(), converted.getState());
        assertEquals(original.getGenres().size(), converted.getGenres().size());
    }
}


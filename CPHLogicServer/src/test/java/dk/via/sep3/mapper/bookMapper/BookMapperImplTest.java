package dk.via.sep3.mapper.bookMapper;

import dk.via.sep3.DTOBook;
import dk.via.sep3.DTOGenre;
import dk.via.sep3.DTOs.book.BookDTO;
import dk.via.sep3.DTOs.book.GenreDTO;
import dk.via.sep3.application.domain.Book;
import dk.via.sep3.application.domain.Genre;
import dk.via.sep3.application.domain.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookMapperImplTest {

  private BookMapperImpl mapper;

  @BeforeEach
  void setUp() {
    mapper = new BookMapperImpl();
  }

  // --------------------------------------------------
  // toDto(Book)
  // --------------------------------------------------

  @Test
  void toDto_mapsAllFieldsCorrectly() {
    Book book = new Book(
        1,
        "123456",
        "Clean Code",
        "Robert Martin",
        State.AVAILABLE,
        List.of(new Genre("Programming"))
    );

    BookDTO dto = mapper.toDto(book);

    assertEquals(1, dto.getId());
    assertEquals("123456", dto.getIsbn());
    assertEquals("Clean Code", dto.getTitle());
    assertEquals("Robert Martin", dto.getAuthor());
    assertEquals("AVAILABLE", dto.getState());
    assertEquals(1, dto.getGenres().size());
    assertEquals("Programming", dto.getGenres().get(0).getName());
  }

  // --------------------------------------------------
  // toDomain(BookDTO)
  // --------------------------------------------------

  @Test
  void toDomain_fromBookDTO_mapsAllFieldsCorrectly() {
    BookDTO dto = new BookDTO(
        2,
        "Domain Driven Design",
        "Eric Evans",
        "987654",
        "BORROWED",
        List.of(new GenreDTO("Software"))
    );

    Book book = mapper.toDomain(dto);

    assertEquals(2, book.getId());
    assertEquals("987654", book.getIsbn());
    assertEquals("Domain Driven Design", book.getTitle());
    assertEquals("Eric Evans", book.getAuthor());
    assertEquals(State.BORROWED, book.getState());
    assertEquals(1, book.getGenres().size());
    assertEquals("Software", book.getGenres().get(0).getName());
  }

  // --------------------------------------------------
  // toDomain(DTOBook) – proto
  // --------------------------------------------------

  @Test
  void toDomain_fromProto_mapsAllFieldsCorrectly() {
    DTOBook protoBook = DTOBook.newBuilder()
        .setId(3)
        .setIsbn("555")
        .setTitle("Refactoring")
        .setAuthor("Martin Fowler")
        .setState("AVAILABLE")
        .addGenres(DTOGenre.newBuilder().setName("Refactor").build())
        .build();

    Book book = mapper.toDomain(protoBook);

    assertEquals(3, book.getId());
    assertEquals("555", book.getIsbn());
    assertEquals("Refactoring", book.getTitle());
    assertEquals("Martin Fowler", book.getAuthor());
    assertEquals(State.AVAILABLE, book.getState());
    assertEquals(1, book.getGenres().size());
    assertEquals("Refactor", book.getGenres().get(0).getName());
  }

  // --------------------------------------------------
  // toProto(Book)
  // --------------------------------------------------

  @Test
  void toProto_mapsAllFieldsCorrectly() {
    Book book = new Book(
        4,
        "777",
        "Effective Java",
        "Joshua Bloch",
        State.AVAILABLE,
        List.of(new Genre("Java"))
    );

    DTOBook proto = mapper.toProto(book);

    assertEquals(4, proto.getId());
    assertEquals("777", proto.getIsbn());
    assertEquals("Effective Java", proto.getTitle());
    assertEquals("Joshua Bloch", proto.getAuthor());
    assertEquals("AVAILABLE", proto.getState());
    assertEquals(1, proto.getGenresList().size());
    assertEquals("Java", proto.getGenresList().get(0).getName());
  }
}

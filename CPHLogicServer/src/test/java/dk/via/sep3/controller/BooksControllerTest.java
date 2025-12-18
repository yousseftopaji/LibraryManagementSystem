package dk.via.sep3.controller;

import dk.via.sep3.application.domain.Book;
import dk.via.sep3.application.services.books.BookService;
import dk.via.sep3.DTOs.book.BookDTO;
import dk.via.sep3.mapper.bookMapper.BookMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BooksControllerTest {

  private BookService bookService;
  private BookMapper bookMapper;
  private BooksController controller;

  private Book book;
  private BookDTO bookDTO;

  @BeforeEach
  void setup() {
    bookService = mock(BookService.class);
    bookMapper = mock(BookMapper.class);
    controller = new BooksController(bookService, bookMapper);

    book = new Book(
        "123",
        "Test Title",
        "Author",
        dk.via.sep3.application.domain.State.AVAILABLE,
        new ArrayList<>()
    );
    book.setId(1);

    bookDTO = new BookDTO();
    bookDTO.setId(1);
    bookDTO.setIsbn("123");
    bookDTO.setTitle("Test Title");
  }

  @Test
  void getAllBooks_returnsListOfBookDTOs() {
    when(bookService.getAllBooks()).thenReturn(List.of(book));
    when(bookMapper.toDto(book)).thenReturn(bookDTO);

    ResponseEntity<List<BookDTO>> response = controller.getAllBooks();

    assertEquals(200, response.getStatusCode().value());
    assertEquals(1, response.getBody().size());
    assertEquals("123", response.getBody().get(0).getIsbn());

    verify(bookService).getAllBooks();
    verify(bookMapper).toDto(book);
  }

  @Test
  void getBookByIsbn_returnsBookDTO() {
    when(bookService.getBookByIsbn("123")).thenReturn(book);
    when(bookMapper.toDto(book)).thenReturn(bookDTO);

    ResponseEntity<BookDTO> response = controller.getBookByIsbn("123");

    assertEquals(200, response.getStatusCode().value());
    assertEquals("123", response.getBody().getIsbn());
    assertEquals("Test Title", response.getBody().getTitle());

    verify(bookService).getBookByIsbn("123");
    verify(bookMapper).toDto(book);
  }
}

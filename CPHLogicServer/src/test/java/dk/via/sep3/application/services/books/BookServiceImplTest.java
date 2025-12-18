package dk.via.sep3.application.services.books;

import dk.via.sep3.application.domain.Book;
import dk.via.sep3.application.domain.State;
import dk.via.sep3.exceptionHandler.ResourceNotFoundException;
import dk.via.sep3.grpcConnection.bookGrpcService.BookGrpcService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

  private BookGrpcService bookGrpcService;
  private BookServiceImpl bookService;

  @BeforeEach
  void setUp() {
    bookGrpcService = mock(BookGrpcService.class);
    bookService = new BookServiceImpl(bookGrpcService);
  }

  // ------------------------------------------------------------
  // getAllBooks()
  // ------------------------------------------------------------

  @Test
  void getAllBooks_removesDuplicateIsbn() {
    Book b1 = new Book("111", "Title1", "Author1", State.AVAILABLE, List.of());
    Book b2 = new Book("111", "Title1", "Author1", State.BORROWED, List.of());
    Book b3 = new Book("222", "Title2", "Author2", State.AVAILABLE, List.of());

    when(bookGrpcService.getAllBooks())
        .thenReturn(List.of(b1, b2, b3));

    List<Book> result = bookService.getAllBooks();

    assertEquals(2, result.size());
    assertEquals("111", result.get(0).getIsbn());
    assertEquals("222", result.get(1).getIsbn());

    verify(bookGrpcService).getAllBooks();
  }

  @Test
  void getAllBooks_emptyList_returnsEmptyList() {
    when(bookGrpcService.getAllBooks()).thenReturn(List.of());

    List<Book> result = bookService.getAllBooks();

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  // ------------------------------------------------------------
  // getBookByIsbn()
  // ------------------------------------------------------------

  @Test
  void getBookByIsbn_returnsAvailableBookIfExists() {
    Book borrowed = new Book("123", "Title", "Author", State.BORROWED, List.of());
    Book available = new Book("123", "Title", "Author", State.AVAILABLE, List.of());

    when(bookGrpcService.getBooksByIsbn("123"))
        .thenReturn(List.of(borrowed, available));

    Book result = bookService.getBookByIsbn("123");

    assertNotNull(result);
    assertEquals(State.AVAILABLE, result.getState());
  }

  @Test
  void getBookByIsbn_returnsFirstBookIfNoAvailable() {
    Book borrowed1 = new Book("123", "Title", "Author", State.BORROWED, List.of());
    Book borrowed2 = new Book("123", "Title", "Author", State.RESERVED, List.of());

    when(bookGrpcService.getBooksByIsbn("123"))
        .thenReturn(List.of(borrowed1, borrowed2));

    Book result = bookService.getBookByIsbn("123");

    assertEquals(borrowed1, result);
  }

  @Test
  void getBookByIsbn_noBooks_throwsException() {
    when(bookGrpcService.getBooksByIsbn("999"))
        .thenReturn(List.of());

    assertThrows(ResourceNotFoundException.class,
        () -> bookService.getBookByIsbn("999"));
  }
}

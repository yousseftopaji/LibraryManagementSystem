package dk.via.sep3.model.books;

import dk.via.sep3.controller.exceptionHandler.ResourceNotFoundException;
import dk.via.sep3.grpcConnection.bookGrpcService.BookGrpcService;
import dk.via.sep3.model.domain.Book;
import dk.via.sep3.model.domain.Genre;
import dk.via.sep3.model.domain.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

  private BookGrpcService grpcService;
  private BookServiceImpl service;

  @BeforeEach
  void setup() {
    grpcService = mock(BookGrpcService.class);
    service = new BookServiceImpl(grpcService);
  }


  // getAllBooks()

  @Test
  void getAllBooks_returnsUniqueBooksByIsbn() {
    Book b1 = new Book("111", "Title1", "AuthorA", State.AVAILABLE, new ArrayList<>());
    Book b2 = new Book("111", "Title1", "AuthorA", State.BORROWED, new ArrayList<>()); // duplicate ISBN
    Book b3 = new Book("222", "Title2", "AuthorB", State.RESERVED, new ArrayList<>());

    when(grpcService.getAllBooks()).thenReturn(Arrays.asList(b1, b2, b3));

    List<Book> result = service.getAllBooks();

    assertEquals(2, result.size());
    assertTrue(result.contains(b1));
    assertTrue(result.contains(b3));
  }

  @Test
  void getAllBooks_returnsEmptyListWhenGrpcReturnsEmpty() {
    when(grpcService.getAllBooks()).thenReturn(Collections.emptyList());

    List<Book> result = service.getAllBooks();

    assertTrue(result.isEmpty());
  }


  // getBookByIsbn()


  @Test
  void getBookByIsbn_returnsAvailableBook() {
    Book borrowed = new Book("123", "T", "A", State.BORROWED, new ArrayList<>());
    Book available = new Book("123", "T", "A", State.AVAILABLE, new ArrayList<>());

    when(grpcService.getBooksByIsbn("123")).thenReturn(Arrays.asList(borrowed, available));

    Book result = service.getBookByIsbn("123");

    assertEquals(State.AVAILABLE, result.getState());
  }

  @Test
  void getBookByIsbn_returnsFirstBookWhenNoneAreAvailable() {
    Book b1 = new Book("123", "T", "A", State.BORROWED, new ArrayList<>());
    Book b2 = new Book("123", "T2", "A2", State.RESERVED, new ArrayList<>());

    when(grpcService.getBooksByIsbn("123")).thenReturn(Arrays.asList(b1, b2));

    Book result = service.getBookByIsbn("123");

    assertEquals(b1, result);
  }

  @Test
  void getBookByIsbn_throwsExceptionWhenNoBooksFound() {
    when(grpcService.getBooksByIsbn("999")).thenReturn(Collections.emptyList());

    assertThrows(ResourceNotFoundException.class, () -> service.getBookByIsbn("999"));
  }
}

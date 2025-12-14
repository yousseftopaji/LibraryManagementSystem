package dk.via.sep3.grpcConnection.bookGrpcService;

import dk.via.sep3.*;
import dk.via.sep3.controller.exceptionHandler.GrpcCommunicationException;
import dk.via.sep3.model.domain.Book;
import dk.via.sep3.shared.mapper.bookMapper.BookMapper;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookGrpcServiceImplTest {

  private BookServiceGrpc.BookServiceBlockingStub stub;
  private BookMapper mapper;
  private BookGrpcServiceImpl service;

  @BeforeEach
  void setup() throws Exception {
    stub = mock(BookServiceGrpc.BookServiceBlockingStub.class);
    mapper = mock(BookMapper.class);

    ManagedChannel channel = mock(ManagedChannel.class);
    service = new BookGrpcServiceImpl(channel, mapper);

    // Inject mocked stub via reflection
    Field stubField = BookGrpcServiceImpl.class.getDeclaredField("bookStub");
    stubField.setAccessible(true);
    stubField.set(service, stub);
  }


  // getAllBooks()


  @Test
  void getAllBooks_returnsMappedBooks() {
    DTOBook dtoBook = DTOBook.newBuilder()
        .setIsbn("123")
        .setTitle("Test Book")
        .build();

    GetAllBooksResponse response = GetAllBooksResponse.newBuilder()
        .addBooks(dtoBook)
        .build();

    Book domainBook = new Book();
    domainBook.setIsbn("123");

    when(stub.getAllBooks(any(GetAllBooksRequest.class))).thenReturn(response);
    when(mapper.toDomain(dtoBook)).thenReturn(domainBook);

    List<Book> result = service.getAllBooks();

    assertEquals(1, result.size());
    assertEquals("123", result.get(0).getIsbn());
  }

  @Test
  void getAllBooks_wrapsGrpcException() {
    when(stub.getAllBooks(any()))
        .thenThrow(new StatusRuntimeException(Status.INTERNAL));

    assertThrows(GrpcCommunicationException.class,
        () -> service.getAllBooks());
  }


  // getBooksByIsbn()


  @Test
  void getBooksByIsbn_returnsMappedBooks() {
    DTOBook dtoBook = DTOBook.newBuilder()
        .setIsbn("999")
        .build();

    GetBooksByIsbnResponse response = GetBooksByIsbnResponse.newBuilder()
        .addBooks(dtoBook)
        .build();

    Book domain = new Book();
    domain.setIsbn("999");

    when(stub.getBooksByIsbn(any(GetBooksByIsbnRequest.class)))
        .thenReturn(response);
    when(mapper.toDomain(dtoBook)).thenReturn(domain);

    List<Book> books = service.getBooksByIsbn("999");

    assertEquals(1, books.size());
    assertEquals("999", books.get(0).getIsbn());
  }

  @Test
  void getBooksByIsbn_wrapsGrpcException() {
    when(stub.getBooksByIsbn(any()))
        .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

    assertThrows(GrpcCommunicationException.class,
        () -> service.getBooksByIsbn("x"));
  }


  // getBookById()


  @Test
  void getBookById_returnsMappedBook() {
    DTOBook dtoBook = DTOBook.newBuilder()
        .setIsbn("abc")
        .build();

    GetBookByIdResponse response = GetBookByIdResponse.newBuilder()
        .setBook(dtoBook)
        .build();

    Book domain = new Book();
    domain.setIsbn("abc");

    when(stub.getBookById(any(GetBookByIdRequest.class)))
        .thenReturn(response);
    when(mapper.toDomain(dtoBook)).thenReturn(domain);

    Book result = service.getBookById(1);

    assertNotNull(result);
    assertEquals("abc", result.getIsbn());
  }

  @Test
  void getBookById_returnsNullOnException() {
    when(stub.getBookById(any()))
        .thenThrow(new RuntimeException());

    Book result = service.getBookById(99);

    assertNull(result);
  }


  // updateBookStatus()


  @Test
  void updateBookStatus_doesNotThrow() {
    UpdateBookStateResponse response =
        UpdateBookStateResponse.newBuilder()
            .setBook(DTOBook.newBuilder().build())
            .build();

    when(stub.updateBookState(any())).thenReturn(response);

    assertDoesNotThrow(() ->
        service.updateBookStatus(1, "BORROWED"));
  }
}

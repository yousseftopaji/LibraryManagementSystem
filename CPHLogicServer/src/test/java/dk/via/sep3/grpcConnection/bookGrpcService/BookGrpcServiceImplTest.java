package dk.via.sep3.grpcConnection.bookGrpcService;

import dk.via.sep3.*;
import dk.via.sep3.application.domain.Book;
import dk.via.sep3.exceptionHandler.GrpcCommunicationException;
import dk.via.sep3.mapper.bookMapper.BookMapper;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookGrpcServiceImplTest {

  private BookServiceGrpc.BookServiceBlockingStub bookStub;
  private BookMapper bookMapper;
  private BookGrpcServiceImpl service;

  @BeforeEach
  void setUp() {
    bookStub = mock(BookServiceGrpc.BookServiceBlockingStub.class);
    bookMapper = mock(BookMapper.class);

    // Fake channel – never used
    ManagedChannel channel = mock(ManagedChannel.class);

    service = new BookGrpcServiceImpl(channel, bookMapper);

    // 🔥 Inject mocked stub via reflection
    injectStub(service, bookStub);
  }

  private void injectStub(BookGrpcServiceImpl service,
      BookServiceGrpc.BookServiceBlockingStub stub) {
    try {
      var field = BookGrpcServiceImpl.class.getDeclaredField("bookStub");
      field.setAccessible(true);
      field.set(service, stub);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // ---------------------------------------------------
  // getAllBooks()
  // ---------------------------------------------------

  @Test
  void getAllBooks_returnsMappedBooks() {
    DTOBook dtoBook = DTOBook.newBuilder().setId(1).setIsbn("123").build();
    GetAllBooksResponse response =
        GetAllBooksResponse.newBuilder().addBooks(dtoBook).build();

    Book domainBook = new Book();

    when(bookStub.getAllBooks(any(GetAllBooksRequest.class)))
        .thenReturn(response);
    when(bookMapper.toDomain(dtoBook)).thenReturn(domainBook);

    List<Book> result = service.getAllBooks();

    assertEquals(1, result.size());
    verify(bookStub).getAllBooks(any(GetAllBooksRequest.class));
    verify(bookMapper).toDomain(dtoBook);
  }

  @Test
  void getAllBooks_grpcError_throwsGrpcCommunicationException() {
    when(bookStub.getAllBooks(any(GetAllBooksRequest.class)))
        .thenThrow(StatusRuntimeException.class);

    assertThrows(GrpcCommunicationException.class,
        () -> service.getAllBooks());
  }

  // ---------------------------------------------------
  // getBooksByIsbn()
  // ---------------------------------------------------

  @Test
  void getBooksByIsbn_returnsMappedBooks() {
    DTOBook dtoBook = DTOBook.newBuilder().setIsbn("123").build();
    GetBooksByIsbnResponse response =
        GetBooksByIsbnResponse.newBuilder().addBooks(dtoBook).build();

    when(bookStub.getBooksByIsbn(any(GetBooksByIsbnRequest.class)))
        .thenReturn(response);
    when(bookMapper.toDomain(dtoBook)).thenReturn(new Book());

    List<Book> result = service.getBooksByIsbn("123");

    assertEquals(1, result.size());
    verify(bookStub).getBooksByIsbn(any(GetBooksByIsbnRequest.class));
  }

  @Test
  void getBooksByIsbn_grpcError_throwsGrpcCommunicationException() {
    when(bookStub.getBooksByIsbn(any(GetBooksByIsbnRequest.class)))
        .thenThrow(StatusRuntimeException.class);

    assertThrows(GrpcCommunicationException.class,
        () -> service.getBooksByIsbn("123"));
  }

  // ---------------------------------------------------
  // getBookById()
  // ---------------------------------------------------

  @Test
  void getBookById_returnsMappedBook() {
    DTOBook dtoBook = DTOBook.newBuilder().setId(1).build();
    GetBookByIdResponse response =
        GetBookByIdResponse.newBuilder().setBook(dtoBook).build();

    when(bookStub.getBookById(any(GetBookByIdRequest.class)))
        .thenReturn(response);
    when(bookMapper.toDomain(dtoBook)).thenReturn(new Book());

    Book result = service.getBookById(1);

    assertNotNull(result);
    verify(bookStub).getBookById(any(GetBookByIdRequest.class));
  }

  // ---------------------------------------------------
  // updateBookStatus()
  // ---------------------------------------------------

  @Test
  void updateBookStatus_executesWithoutException() {
    UpdateBookStateResponse response =
        UpdateBookStateResponse.newBuilder()
            .setBook(DTOBook.newBuilder().setId(1).build())
            .build();

    when(bookStub.updateBookState(any(UpdateBookStateRequest.class)))
        .thenReturn(response);

    assertDoesNotThrow(() ->
        service.updateBookStatus(1, "Borrowed"));

    verify(bookStub).updateBookState(any(UpdateBookStateRequest.class));
  }
}

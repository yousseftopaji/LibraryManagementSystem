package dk.via.sep3.grpcConnection.bookGrpcService;

import dk.via.sep3.*;
import dk.via.sep3.exceptionHandler.GrpcCommunicationException;
import dk.via.sep3.mapper.bookMapper.BookMapper;
import dk.via.sep3.application.domain.Book;
import dk.via.sep3.application.domain.State;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookGrpcServiceImplTest {

    @Mock
    private ManagedChannel channel;

    @Mock
    private BookServiceGrpc.BookServiceBlockingStub bookStub;

    @Mock
    private BookMapper bookMapper;

    private BookGrpcServiceImpl bookGrpcService;
    private MockedStatic<BookServiceGrpc> mockedStatic;

    @BeforeEach
    void setUp() {
        mockedStatic = mockStatic(BookServiceGrpc.class);
        mockedStatic.when(() -> BookServiceGrpc.newBlockingStub(channel)).thenReturn(bookStub);
        bookGrpcService = new BookGrpcServiceImpl(channel, bookMapper);
    }

    @AfterEach
    void tearDown() {
        if (mockedStatic != null) {
            mockedStatic.close();
        }
    }

    @Test
    @DisplayName("Should get all books successfully")
    void testGetAllBooks_Success() {
        // Arrange
        DTOBook dtoBook = DTOBook.newBuilder()
            .setId(1)
            .setIsbn("123456")
            .setTitle("Test Book")
            .setAuthor("Test Author")
            .setState("AVAILABLE")
            .build();

        GetAllBooksResponse response = GetAllBooksResponse.newBuilder()
            .addBooks(dtoBook)
            .build();

        Book book = new Book(1, "123456", "Test Book", "Test Author", State.AVAILABLE, new ArrayList<>());

        when(bookStub.getAllBooks(any(GetAllBooksRequest.class))).thenReturn(response);
        when(bookMapper.toDomain(dtoBook)).thenReturn(book);

        // Act
        List<Book> result = bookGrpcService.getAllBooks();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Book", result.get(0).getTitle());
        verify(bookStub).getAllBooks(any(GetAllBooksRequest.class));
    }

    @Test
    @DisplayName("Should throw GrpcCommunicationException when gRPC fails on getAllBooks")
    void testGetAllBooks_GrpcFailure() {
        // Arrange
        when(bookStub.getAllBooks(any(GetAllBooksRequest.class)))
            .thenThrow(new StatusRuntimeException(io.grpc.Status.UNAVAILABLE));

        // Act & Assert
        assertThrows(GrpcCommunicationException.class, () -> bookGrpcService.getAllBooks());
    }

    @Test
    @DisplayName("Should get books by ISBN successfully")
    void testGetBooksByIsbn_Success() {
        // Arrange
        String isbn = "123456";
        DTOBook dtoBook = DTOBook.newBuilder()
            .setId(1)
            .setIsbn(isbn)
            .setTitle("Test Book")
            .setAuthor("Test Author")
            .setState("AVAILABLE")
            .build();

        GetBooksByIsbnResponse response = GetBooksByIsbnResponse.newBuilder()
            .addBooks(dtoBook)
            .build();

        Book book = new Book(1, isbn, "Test Book", "Test Author", State.AVAILABLE, new ArrayList<>());

        when(bookStub.getBooksByIsbn(any(GetBooksByIsbnRequest.class))).thenReturn(response);
        when(bookMapper.toDomain(dtoBook)).thenReturn(book);

        // Act
        List<Book> result = bookGrpcService.getBooksByIsbn(isbn);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(isbn, result.get(0).getIsbn());
        verify(bookStub).getBooksByIsbn(any(GetBooksByIsbnRequest.class));
    }

    @Test
    @DisplayName("Should throw GrpcCommunicationException when gRPC fails on getBooksByIsbn")
    void testGetBooksByIsbn_GrpcFailure() {
        // Arrange
        String isbn = "123456";
        when(bookStub.getBooksByIsbn(any(GetBooksByIsbnRequest.class)))
            .thenThrow(new StatusRuntimeException(io.grpc.Status.UNAVAILABLE));

        // Act & Assert
        assertThrows(GrpcCommunicationException.class, () -> bookGrpcService.getBooksByIsbn(isbn));
    }

    @Test
    @DisplayName("Should get book by ID successfully")
    void testGetBookById_Success() {
        // Arrange
        int bookId = 1;
        DTOBook dtoBook = DTOBook.newBuilder()
            .setId(bookId)
            .setIsbn("123456")
            .setTitle("Test Book")
            .setAuthor("Test Author")
            .setState("AVAILABLE")
            .build();

        GetBookByIdResponse response = GetBookByIdResponse.newBuilder()
            .setBook(dtoBook)
            .build();

        Book book = new Book(bookId, "123456", "Test Book", "Test Author", State.AVAILABLE, new ArrayList<>());

        when(bookStub.getBookById(any(GetBookByIdRequest.class))).thenReturn(response);
        when(bookMapper.toDomain(dtoBook)).thenReturn(book);

        // Act
        Book result = bookGrpcService.getBookById(bookId);

        // Assert
        assertNotNull(result);
        assertEquals(bookId, result.getId());
        verify(bookStub).getBookById(any(GetBookByIdRequest.class));
    }

    @Test
    @DisplayName("Should return null when getBookById fails")
    void testGetBookById_Failure() {
        // Arrange
        int bookId = 1;
        when(bookStub.getBookById(any(GetBookByIdRequest.class)))
            .thenThrow(new RuntimeException("Failed"));

        // Act
        Book result = bookGrpcService.getBookById(bookId);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Should update book status successfully")
    void testUpdateBookStatus_Success() {
        // Arrange
        int bookId = 1;
        String status = "BORROWED";
        DTOBook dtoBook = DTOBook.newBuilder()
            .setId(bookId)
            .setState(status)
            .build();

        UpdateBookStateResponse response = UpdateBookStateResponse.newBuilder()
            .setBook(dtoBook)
            .build();

        Book book = new Book();
        book.setId(bookId);
        book.setState(State.BORROWED);

        when(bookStub.updateBookState(any(UpdateBookStateRequest.class))).thenReturn(response);
        when(bookMapper.toDomain(dtoBook)).thenReturn(book);

        // Act
        assertDoesNotThrow(() -> bookGrpcService.updateBookStatus(bookId, status));

        // Assert
        verify(bookStub).updateBookState(any(UpdateBookStateRequest.class));
    }

    @Test
    @DisplayName("Should handle exception when updateBookStatus fails")
    void testUpdateBookStatus_Failure() {
        // Arrange
        int bookId = 1;
        String status = "BORROWED";
        when(bookStub.updateBookState(any(UpdateBookStateRequest.class)))
            .thenThrow(new RuntimeException("Failed"));

        // Act & Assert - should not throw, just log
        assertDoesNotThrow(() -> bookGrpcService.updateBookStatus(bookId, status));
    }

    @Test
    @DisplayName("Should return empty list when no books found")
    void testGetAllBooks_EmptyList() {
        // Arrange
        GetAllBooksResponse response = GetAllBooksResponse.newBuilder().build();
        when(bookStub.getAllBooks(any(GetAllBooksRequest.class))).thenReturn(response);

        // Act
        List<Book> result = bookGrpcService.getAllBooks();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle multiple books in response")
    void testGetAllBooks_MultipleBooks() {
        // Arrange
        DTOBook book1 = DTOBook.newBuilder().setId(1).setTitle("Book 1").build();
        DTOBook book2 = DTOBook.newBuilder().setId(2).setTitle("Book 2").build();

        GetAllBooksResponse response = GetAllBooksResponse.newBuilder()
            .addBooks(book1)
            .addBooks(book2)
            .build();

        Book domainBook1 = new Book();
        domainBook1.setId(1);
        Book domainBook2 = new Book();
        domainBook2.setId(2);

        when(bookStub.getAllBooks(any(GetAllBooksRequest.class))).thenReturn(response);
        when(bookMapper.toDomain(book1)).thenReturn(domainBook1);
        when(bookMapper.toDomain(book2)).thenReturn(domainBook2);

        // Act
        List<Book> result = bookGrpcService.getAllBooks();

        // Assert
        assertEquals(2, result.size());
    }
}


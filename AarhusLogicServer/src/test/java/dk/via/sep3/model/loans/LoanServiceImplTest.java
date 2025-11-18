//package dk.via.sep3.model.loans;
//
//import dk.via.sep3.DTOBook;
//import dk.via.sep3.DTOLoan;
//import dk.via.sep3.grpcConnection.bookGrpcService.BookGrpcService;
//import dk.via.sep3.grpcConnection.bookGrpcService.BookGrpcServiceImpl;
//import dk.via.sep3.grpcConnection.loanGrpcService.LoanGrpcService;
//import dk.via.sep3.grpcConnection.loanGrpcService.LoanGrpcServiceImpl;
//import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
//import dk.via.sep3.model.users.UserService;
//import dk.via.sep3.shared.LoanDTO;
//import dk.via.sep3.shared.UserDTO;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.DisplayName;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
///**
// * Unit tests for LoanServiceImpl
// * Tests loan creation with various scenarios including validation and error handling
// */
//class LoanServiceImplTest
//{
//    @Mock
//    private LoanGrpcService loanGrpcService;
//    private BookGrpcService bookGrpcService;
//    private UserGrpcService userGrpcService;
//
//    @InjectMocks
//    private LoanGrpcServiceImpl loanGrpcServiceImpl;
//    private BookGrpcServiceImpl bookGrpcServiceImpl;
//
//    private DTOBook availableBook;
//    private DTOBook borrowedBook;
//    private DTOLoan mockLoan;
//    private UserDTO validUser;
//
//    @BeforeEach
//    void setUp()
//    {
//        MockitoAnnotations.openMocks(this);
//
//        // Setup mock available book
//        availableBook = DTOBook.newBuilder()
//                .setId(1)
//                .setTitle("Clean Code")
//                .setAuthor("Robert C. Martin")
//                .setIsbn("9780132350884")
//                .setState("AVAILABLE")  // Must match LoanServiceImpl.equals("AVAILABLE")
//                .build();
//
//        // Setup mock borrowed book
//        borrowedBook = DTOBook.newBuilder()
//                .setId(2)
//                .setTitle("Test Book")
//                .setAuthor("Test Author")
//                .setIsbn("1234567890")
//                .setState("Borrowed")
//                .build();
//
//        // Setup mock loan
//        mockLoan = DTOLoan.newBuilder()
//                .setId(1)
//                .setBorrowDate("2025-11-18")
//                .setDueDate("2025-12-02")
//                .setUsername("stud.alex")
//                .setBookId(1)
//                .build();
//
//        // Setup valid user
//        validUser = new UserDTO();
//        validUser.setUsername("stud.alex");
//    }
//
//    @Test
//    @DisplayName("Should successfully create loan when all validations pass")
//    void testCreateLoan_Success()
//    {
//        // Arrange
//        String username = "stud.alex";
//        String bookId = "1";
//        int loanDuration = 14;
//
//        List<DTOBook> books = new ArrayList<>();
//        books.add(availableBook);
//
//        when(userGrpcService.getUserByUsername(username)).thenReturn(validUser);
//        when(grpcConnectionInterface.getAllBooks()).thenReturn(books);
//        when(grpcConnectionInterface.createLoan(username, bookId, loanDuration)).thenReturn(mockLoan);
//
//        // Act
//        LoanDTO result = loanService.createLoan(username, bookId, loanDuration);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("1", result.getId());
//        assertEquals("2025-11-18", result.getBorrowDate());
//        assertEquals("2025-12-02", result.getDueDate());
//        assertEquals("stud.alex", result.getUsername());
//        assertEquals("1", result.getBookId());
//        assertFalse(result.isReturned());
//        assertEquals(0, result.getNumberOfExtensions());
//
//        // Verify interactions
//        verify(userService, times(1)).getUserByUsername(username);
//        verify(grpcConnectionInterface, times(1)).getAllBooks();
//        verify(grpcConnectionInterface, times(1)).createLoan(username, bookId, loanDuration);
//    }
//
//    @Test
//    @DisplayName("Should throw IllegalArgumentException when loan duration is zero")
//    void testCreateLoan_InvalidDuration_Zero()
//    {
//        // Arrange
//        String username = "stud.alex";
//        String bookId = "1";
//        int loanDuration = 0;
//
//        // Act & Assert
//        IllegalArgumentException exception = assertThrows(
//                IllegalArgumentException.class,
//                () -> loanService.createLoan(username, bookId, loanDuration)
//        );
//
//        assertTrue(exception.getMessage().contains("Loan duration must be positive"));
//        verify(userService, never()).getUserByUsername(anyString());
//        verify(grpcConnectionInterface, never()).getAllBooks();
//    }
//
//    @Test
//    @DisplayName("Should throw IllegalArgumentException when loan duration is negative")
//    void testCreateLoan_InvalidDuration_Negative()
//    {
//        // Arrange
//        String username = "stud.alex";
//        String bookId = "1";
//        int loanDuration = -5;
//
//        // Act & Assert
//        IllegalArgumentException exception = assertThrows(
//                IllegalArgumentException.class,
//                () -> loanService.createLoan(username, bookId, loanDuration)
//        );
//
//        assertTrue(exception.getMessage().contains("Loan duration must be positive"));
//        assertTrue(exception.getMessage().contains("-5 days"));
//    }
//
//    @Test
//    @DisplayName("Should throw IllegalArgumentException when user does not exist")
//    void testCreateLoan_UserNotFound()
//    {
//        // Arrange
//        String username = "nonexistent.user";
//        String bookId = "1";
//        int loanDuration = 14;
//
//        when(userService.getUserByUsername(username)).thenReturn(null);
//
//        // Act & Assert
//        IllegalArgumentException exception = assertThrows(
//                IllegalArgumentException.class,
//                () -> loanService.createLoan(username, bookId, loanDuration)
//        );
//
//        assertTrue(exception.getMessage().contains("User not found"));
//        assertTrue(exception.getMessage().contains(username));
//        verify(userService, times(1)).getUserByUsername(username);
//        verify(grpcConnectionInterface, never()).getAllBooks();
//    }
//
//    @Test
//    @DisplayName("Should throw IllegalArgumentException when book does not exist")
//    void testCreateLoan_BookNotFound()
//    {
//        // Arrange
//        String username = "stud.alex";
//        String bookId = "999";
//        int loanDuration = 14;
//
//        List<DTOBook> books = new ArrayList<>();
//        books.add(availableBook); // Book ID 1, not 999
//
//        when(userService.getUserByUsername(username)).thenReturn(validUser);
//        when(grpcConnectionInterface.getAllBooks()).thenReturn(books);
//
//        // Act & Assert
//        IllegalArgumentException exception = assertThrows(
//                IllegalArgumentException.class,
//                () -> loanService.createLoan(username, bookId, loanDuration)
//        );
//
//        assertTrue(exception.getMessage().contains("Book not found"));
//        assertTrue(exception.getMessage().contains("999"));
//        verify(grpcConnectionInterface, times(1)).getAllBooks();
//        verify(grpcConnectionInterface, never()).createLoan(anyString(), anyString(), anyInt());
//    }
//
//    @Test
//    @DisplayName("Should throw IllegalStateException when book is not available")
//    void testCreateLoan_BookNotAvailable()
//    {
//        // Arrange
//        String username = "stud.alex";
//        String bookId = "2";
//        int loanDuration = 14;
//
//        List<DTOBook> books = new ArrayList<>();
//        books.add(borrowedBook); // This book is already borrowed
//
//        when(userService.getUserByUsername(username)).thenReturn(validUser);
//        when(grpcConnectionInterface.getAllBooks()).thenReturn(books);
//
//        // Act & Assert
//        IllegalStateException exception = assertThrows(
//                IllegalStateException.class,
//                () -> loanService.createLoan(username, bookId, loanDuration)
//        );
//
//        assertTrue(exception.getMessage().contains("not available"));
//        assertTrue(exception.getMessage().contains("Borrowed"));
//        verify(grpcConnectionInterface, never()).createLoan(anyString(), anyString(), anyInt());
//    }
//
//    @Test
//    @DisplayName("Should throw RuntimeException when gRPC returns null")
//    void testCreateLoan_GrpcReturnsNull()
//    {
//        // Arrange
//        String username = "stud.alex";
//        String bookId = "1";
//        int loanDuration = 14;
//
//        List<DTOBook> books = new ArrayList<>();
//        books.add(availableBook);
//
//        when(userService.getUserByUsername(username)).thenReturn(validUser);
//        when(grpcConnectionInterface.getAllBooks()).thenReturn(books);
//        when(grpcConnectionInterface.createLoan(username, bookId, loanDuration)).thenReturn(null);
//
//        // Act & Assert
//        RuntimeException exception = assertThrows(
//                RuntimeException.class,
//                () -> loanService.createLoan(username, bookId, loanDuration)
//        );
//
//        assertNotNull(exception.getMessage());
//        assertTrue(exception.getMessage().toLowerCase().contains("failed"));
//        verify(grpcConnectionInterface, times(1)).createLoan(username, bookId, loanDuration);
//    }
//
//    @Test
//    @DisplayName("Should throw RuntimeException when gRPC returns invalid loan ID")
//    void testCreateLoan_GrpcReturnsInvalidId()
//    {
//        // Arrange
//        String username = "stud.alex";
//        String bookId = "1";
//        int loanDuration = 14;
//
//        List<DTOBook> books = new ArrayList<>();
//        books.add(availableBook);
//
//        DTOLoan invalidLoan = DTOLoan.newBuilder()
//                .setId(0) // Invalid ID
//                .setBorrowDate("2025-11-18")
//                .setDueDate("2025-12-02")
//                .setUsername("stud.alex")
//                .setBookId(1)
//                .build();
//
//        when(userService.getUserByUsername(username)).thenReturn(validUser);
//        when(grpcConnectionInterface.getAllBooks()).thenReturn(books);
//        when(grpcConnectionInterface.createLoan(username, bookId, loanDuration)).thenReturn(invalidLoan);
//
//        // Act & Assert
//        RuntimeException exception = assertThrows(
//                RuntimeException.class,
//                () -> loanService.createLoan(username, bookId, loanDuration)
//        );
//
//        assertNotNull(exception.getMessage());
//        assertTrue(exception.getMessage().toLowerCase().contains("failed"));
//    }
//
//    @Test
//    @DisplayName("Should successfully create loan with minimum valid duration")
//    void testCreateLoan_MinimumDuration()
//    {
//        // Arrange
//        String username = "stud.alex";
//        String bookId = "1";
//        int loanDuration = 1; // Minimum valid duration
//
//        List<DTOBook> books = new ArrayList<>();
//        books.add(availableBook);
//
//        when(userService.getUserByUsername(username)).thenReturn(validUser);
//        when(grpcConnectionInterface.getAllBooks()).thenReturn(books);
//        when(grpcConnectionInterface.createLoan(username, bookId, loanDuration)).thenReturn(mockLoan);
//
//        // Act
//        LoanDTO result = loanService.createLoan(username, bookId, loanDuration);
//
//        // Assert
//        assertNotNull(result);
//        verify(grpcConnectionInterface, times(1)).createLoan(username, bookId, loanDuration);
//    }
//
//    @Test
//    @DisplayName("Should successfully create loan with maximum typical duration")
//    void testCreateLoan_MaximumTypicalDuration()
//    {
//        // Arrange
//        String username = "stud.alex";
//        String bookId = "1";
//        int loanDuration = 90; // 3 months
//
//        List<DTOBook> books = new ArrayList<>();
//        books.add(availableBook);
//
//        when(userService.getUserByUsername(username)).thenReturn(validUser);
//        when(grpcConnectionInterface.getAllBooks()).thenReturn(books);
//        when(grpcConnectionInterface.createLoan(username, bookId, loanDuration)).thenReturn(mockLoan);
//
//        // Act
//        LoanDTO result = loanService.createLoan(username, bookId, loanDuration);
//
//        // Assert
//        assertNotNull(result);
//        verify(grpcConnectionInterface, times(1)).createLoan(username, bookId, loanDuration);
//    }
//
//    @Test
//    @DisplayName("Should find correct book among multiple books")
//    void testCreateLoan_MultipleBooks()
//    {
//        // Arrange
//        String username = "stud.alex";
//        String bookId = "1";
//        int loanDuration = 14;
//
//        List<DTOBook> books = new ArrayList<>();
//        books.add(borrowedBook); // ID 2
//        books.add(availableBook); // ID 1
//
//        DTOBook anotherBook = DTOBook.newBuilder()
//                .setId(3)
//                .setTitle("Another Book")
//                .setAuthor("Another Author")
//                .setIsbn("1111111111")
//                .setState("AVAILABLE")  // Must be AVAILABLE (all caps)
//                .build();
//        books.add(anotherBook);
//
//        when(userService.getUserByUsername(username)).thenReturn(validUser);
//        when(grpcConnectionInterface.getAllBooks()).thenReturn(books);
//        when(grpcConnectionInterface.createLoan(username, bookId, loanDuration)).thenReturn(mockLoan);
//
//        // Act
//        LoanDTO result = loanService.createLoan(username, bookId, loanDuration);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("1", result.getBookId());
//        verify(grpcConnectionInterface, times(1)).createLoan(username, bookId, loanDuration);
//    }
//
//    @Test
//    @DisplayName("Should handle empty book list")
//    void testCreateLoan_EmptyBookList()
//    {
//        // Arrange
//        String username = "stud.alex";
//        String bookId = "1";
//        int loanDuration = 14;
//
//        List<DTOBook> books = new ArrayList<>(); // Empty list
//
//        when(userService.getUserByUsername(username)).thenReturn(validUser);
//        when(grpcConnectionInterface.getAllBooks()).thenReturn(books);
//
//        // Act & Assert
//        IllegalArgumentException exception = assertThrows(
//                IllegalArgumentException.class,
//                () -> loanService.createLoan(username, bookId, loanDuration)
//        );
//
//        assertTrue(exception.getMessage().contains("Book not found"));
//    }
//}
//

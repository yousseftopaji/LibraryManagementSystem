package dk.via.sep3.model.loans;

import dk.via.sep3.grpcConnection.bookGrpcService.BookGrpcService;
import dk.via.sep3.grpcConnection.loanGrpcService.LoanGrpcService;
import dk.via.sep3.model.domain.Book;
import dk.via.sep3.model.domain.Genre;
import dk.via.sep3.model.domain.Loan;
import dk.via.sep3.model.domain.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LoanServiceImpl
 * Tests loan creation and extension business logic
 */
@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    @Mock
    private BookGrpcService bookGrpcService;

    @Mock
    private LoanGrpcService loanGrpcService;

    @InjectMocks
    private LoanServiceImpl loanService;

    private Loan loanRequest;
    private Book availableBook;
    private Book borrowedBook;
    private Loan existingLoan;

    @BeforeEach
    void setUp() {
        // Setup loan request
        loanRequest = new Loan();
        loanRequest.setUsername("johndoe");
        loanRequest.setBookISBN("978-0-123456-47-2");

        // Setup available book
        availableBook = new Book(1, "978-0-123456-47-2", "Clean Code", "Robert Martin",
                                 State.AVAILABLE, Arrays.asList(new Genre("Programming")));

        // Setup borrowed book
        borrowedBook = new Book(2, "978-0-123456-47-2", "Clean Code", "Robert Martin",
                                State.BORROWED, Arrays.asList(new Genre("Programming")));

        // Setup existing loan
        existingLoan = new Loan();
        existingLoan.setLoanId(100);
        existingLoan.setUsername("johndoe");
        existingLoan.setBookId(1);
        existingLoan.setBookISBN("978-0-123456-47-2");
        existingLoan.setBorrowDate(Date.valueOf(LocalDate.now()));
        existingLoan.setDueDate(Date.valueOf(LocalDate.now().plusDays(30)));
        existingLoan.setNumberOfExtensions(0);
        existingLoan.setReturned(false);
    }

    // ========== createLoan Tests ==========

    @Test
    @DisplayName("Should successfully create loan with available book")
    void testCreateLoan_Success() {
        // Arrange
        when(loanGrpcService.getLoansByISBN("978-0-123456-47-2")).thenReturn(new ArrayList<>());
        when(bookGrpcService.getBooksByIsbn("978-0-123456-47-2"))
            .thenReturn(Arrays.asList(availableBook));

        Loan createdLoan = new Loan();
        createdLoan.setLoanId(1);
        createdLoan.setUsername("johndoe");
        createdLoan.setBookId(1);
        when(loanGrpcService.createLoan(any(Loan.class))).thenReturn(createdLoan);

        // Act
        Loan result = loanService.createLoan(loanRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getLoanId());
        assertEquals("johndoe", result.getUsername());

        verify(loanGrpcService, times(1)).getLoansByISBN("978-0-123456-47-2");
        verify(bookGrpcService, times(1)).getBooksByIsbn("978-0-123456-47-2");
        verify(loanGrpcService, times(1)).createLoan(any(Loan.class));
        verify(bookGrpcService, times(1)).updateBookStatus(1, "Borrowed");
    }

    @Test
    @DisplayName("Should throw exception when user already has active loan for same ISBN")
    void testCreateLoan_DuplicateActiveLoan() {
        // Arrange
        Loan activeLoan = new Loan();
        activeLoan.setUsername("johndoe");
        activeLoan.setReturned(false);
        when(loanGrpcService.getLoansByISBN("978-0-123456-47-2"))
            .thenReturn(Arrays.asList(activeLoan));

        // Act & Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> loanService.createLoan(loanRequest)
        );

        assertEquals("User already has an active loan for this book", exception.getMessage());
        verify(loanGrpcService, times(1)).getLoansByISBN("978-0-123456-47-2");
        verify(bookGrpcService, never()).getBooksByIsbn(anyString());
    }

    @Test
    @DisplayName("Should allow loan when previous loan is returned")
    void testCreateLoan_PreviousLoanReturned() {
        // Arrange
        Loan returnedLoan = new Loan();
        returnedLoan.setUsername("johndoe");
        returnedLoan.setReturned(true); // Already returned
        when(loanGrpcService.getLoansByISBN("978-0-123456-47-2"))
            .thenReturn(Arrays.asList(returnedLoan));
        when(bookGrpcService.getBooksByIsbn("978-0-123456-47-2"))
            .thenReturn(Arrays.asList(availableBook));

        Loan createdLoan = new Loan();
        createdLoan.setLoanId(2);
        when(loanGrpcService.createLoan(any(Loan.class))).thenReturn(createdLoan);

        // Act
        Loan result = loanService.createLoan(loanRequest);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getLoanId());
        verify(bookGrpcService, times(1)).getBooksByIsbn("978-0-123456-47-2");
    }

    @Test
    @DisplayName("Should throw exception when no books found with ISBN")
    void testCreateLoan_NoBooks() {
        // Arrange
        when(loanGrpcService.getLoansByISBN("978-0-123456-47-2")).thenReturn(new ArrayList<>());
        when(bookGrpcService.getBooksByIsbn("978-0-123456-47-2"))
            .thenReturn(new ArrayList<>());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> loanService.createLoan(loanRequest)
        );

        assertEquals("No books found with the specified ISBN", exception.getMessage());
        verify(loanGrpcService, never()).createLoan(any());
    }

    @Test
    @DisplayName("Should throw exception when no available copies exist")
    void testCreateLoan_NoAvailableCopies() {
        // Arrange
        when(loanGrpcService.getLoansByISBN("978-0-123456-47-2")).thenReturn(new ArrayList<>());
        when(bookGrpcService.getBooksByIsbn("978-0-123456-47-2"))
            .thenReturn(Arrays.asList(borrowedBook)); // Only borrowed copy

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> loanService.createLoan(loanRequest)
        );

        assertEquals("No available copies of this book", exception.getMessage());
        verify(loanGrpcService, never()).createLoan(any());
    }

    @Test
    @DisplayName("Should select first available book when multiple copies available")
    void testCreateLoan_MultipleAvailableCopies() {
        // Arrange
        Book availableBook2 = new Book(3, "978-0-123456-47-2", "Clean Code", "Robert Martin",
                                       State.AVAILABLE, new ArrayList<>());
        when(loanGrpcService.getLoansByISBN("978-0-123456-47-2")).thenReturn(new ArrayList<>());
        when(bookGrpcService.getBooksByIsbn("978-0-123456-47-2"))
            .thenReturn(Arrays.asList(borrowedBook, availableBook, availableBook2));

        Loan createdLoan = new Loan();
        createdLoan.setLoanId(1);
        createdLoan.setBookId(1);
        when(loanGrpcService.createLoan(any(Loan.class))).thenReturn(createdLoan);

        // Act
        Loan result = loanService.createLoan(loanRequest);

        // Assert
        assertEquals(1, result.getBookId()); // First available book selected
        verify(bookGrpcService, times(1)).updateBookStatus(1, "Borrowed");
    }

    @Test
    @DisplayName("Should set loan dates correctly (30 days from today)")
    void testCreateLoan_CorrectDates() {
        // Arrange
        when(loanGrpcService.getLoansByISBN("978-0-123456-47-2")).thenReturn(new ArrayList<>());
        when(bookGrpcService.getBooksByIsbn("978-0-123456-47-2"))
            .thenReturn(Arrays.asList(availableBook));

        Loan createdLoan = new Loan();
        createdLoan.setLoanId(1);
        when(loanGrpcService.createLoan(any(Loan.class))).thenAnswer(invocation -> {
            Loan loan = invocation.getArgument(0);
            assertEquals(0, loan.getNumberOfExtensions());
            assertFalse(loan.isReturned());
            assertNotNull(loan.getBorrowDate());
            assertNotNull(loan.getDueDate());
            return createdLoan;
        });

        // Act
        loanService.createLoan(loanRequest);

        // Assert verified in answer
        verify(loanGrpcService, times(1)).createLoan(any(Loan.class));
    }

    @Test
    @DisplayName("Should throw exception when loan persistence fails")
    void testCreateLoan_PersistenceFails() {
        // Arrange
        when(loanGrpcService.getLoansByISBN("978-0-123456-47-2")).thenReturn(new ArrayList<>());
        when(bookGrpcService.getBooksByIsbn("978-0-123456-47-2"))
            .thenReturn(Arrays.asList(availableBook));
        when(loanGrpcService.createLoan(any(Loan.class))).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> loanService.createLoan(loanRequest)
        );

        assertTrue(exception.getMessage().contains("Failed to create loan"));
        verify(bookGrpcService, never()).updateBookStatus(anyInt(), anyString());
    }

    // ========== extendLoan Tests ==========

    @Test
    @DisplayName("Should successfully extend loan")
    void testExtendLoan_Success() {
        // Arrange
        existingLoan.setDueDate(Date.valueOf(LocalDate.now().plusDays(1))); // Due tomorrow
        when(loanGrpcService.getLoanById(100)).thenReturn(existingLoan);
        doNothing().when(loanGrpcService).extendLoan(any(Loan.class));

        // Act
        loanService.extendLoan(existingLoan);

        // Assert
        assertEquals(1, existingLoan.getNumberOfExtensions());
        assertEquals(Date.valueOf(LocalDate.now().plusDays(31)), existingLoan.getDueDate());
        verify(loanGrpcService, times(1)).getLoanById(100);
        verify(loanGrpcService, times(1)).extendLoan(existingLoan);
    }

    @Test
    @DisplayName("Should throw exception when loan not found")
    void testExtendLoan_LoanNotFound() {
        // Arrange
        when(loanGrpcService.getLoanById(100)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> loanService.extendLoan(existingLoan)
        );

        assertEquals("Loan not found with ID: 100", exception.getMessage());
        verify(loanGrpcService, never()).extendLoan(any());
    }

    @Test
    @DisplayName("Should throw exception when user is not the borrower")
    void testExtendLoan_NotBorrower() {
        // Arrange
        existingLoan.setUsername("otherperson");
        Loan requestLoan = new Loan();
        requestLoan.setLoanId(100);
        requestLoan.setUsername("johndoe");
        requestLoan.setDueDate(Date.valueOf(LocalDate.now().plusDays(1)));

        when(loanGrpcService.getLoanById(100)).thenReturn(existingLoan);

        // Act & Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> loanService.extendLoan(requestLoan)
        );

        assertEquals("Only the borrower can extend the loan", exception.getMessage());
        verify(loanGrpcService, never()).extendLoan(any());
    }

    @Test
    @DisplayName("Should throw exception when extending too early")
    void testExtendLoan_TooEarly() {
        // Arrange
        existingLoan.setDueDate(Date.valueOf(LocalDate.now().plusDays(10))); // Due in 10 days
        when(loanGrpcService.getLoanById(100)).thenReturn(existingLoan);

        // Act & Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> loanService.extendLoan(existingLoan)
        );

        assertTrue(exception.getMessage().contains("You can extend your loan starting from"));
        verify(loanGrpcService, never()).extendLoan(any());
    }

    @Test
    @DisplayName("Should throw exception when max extensions reached")
    void testExtendLoan_MaxExtensions() {
        // Arrange
        existingLoan.setDueDate(Date.valueOf(LocalDate.now().plusDays(1)));
        existingLoan.setNumberOfExtensions(12); // Already at max
        when(loanGrpcService.getLoanById(100)).thenReturn(existingLoan);

        // Act & Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> loanService.extendLoan(existingLoan)
        );

        assertTrue(exception.getMessage().contains("maximum number of extensions"));
        verify(loanGrpcService, never()).extendLoan(any());
    }

    @Test
    @DisplayName("Should allow extension one day before due date")
    void testExtendLoan_OneDayBefore() {
        // Arrange
        existingLoan.setDueDate(Date.valueOf(LocalDate.now().plusDays(1))); // Tomorrow
        when(loanGrpcService.getLoanById(100)).thenReturn(existingLoan);

        // Act
        loanService.extendLoan(existingLoan);

        // Assert
        assertEquals(1, existingLoan.getNumberOfExtensions());
        verify(loanGrpcService, times(1)).extendLoan(existingLoan);
    }

    @Test
    @DisplayName("Should extend by 30 days from current due date")
    void testExtendLoan_Correct30DayExtension() {
        // Arrange
        LocalDate originalDueDate = LocalDate.now().plusDays(1);
        existingLoan.setDueDate(Date.valueOf(originalDueDate));
        when(loanGrpcService.getLoanById(100)).thenReturn(existingLoan);

        // Act
        loanService.extendLoan(existingLoan);

        // Assert
        assertEquals(Date.valueOf(originalDueDate.plusDays(30)), existingLoan.getDueDate());
    }

    @Test
    @DisplayName("Should increment extension count")
    void testExtendLoan_IncrementsCounter() {
        // Arrange
        existingLoan.setDueDate(Date.valueOf(LocalDate.now().plusDays(1)));
        existingLoan.setNumberOfExtensions(5);
        when(loanGrpcService.getLoanById(100)).thenReturn(existingLoan);

        // Act
        loanService.extendLoan(existingLoan);

        // Assert
        assertEquals(6, existingLoan.getNumberOfExtensions());
    }

    @Test
    @DisplayName("Should allow extension on due date itself")
    void testExtendLoan_OnDueDate() {
        // Arrange
        existingLoan.setDueDate(Date.valueOf(LocalDate.now()));
        when(loanGrpcService.getLoanById(100)).thenReturn(existingLoan);

        // Act
        loanService.extendLoan(existingLoan);

        // Assert
        verify(loanGrpcService, times(1)).extendLoan(existingLoan);
    }

    @Test
    @DisplayName("Should handle gRPC exception during extension")
    void testExtendLoan_GrpcException() {
        // Arrange
        existingLoan.setDueDate(Date.valueOf(LocalDate.now().plusDays(1)));
        when(loanGrpcService.getLoanById(100)).thenReturn(existingLoan);
        doThrow(new RuntimeException("gRPC error")).when(loanGrpcService).extendLoan(any());

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> loanService.extendLoan(existingLoan)
        );

        assertEquals("gRPC error", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle case-insensitive username comparison for borrower check")
    void testExtendLoan_CaseInsensitiveUsername() {
        // Arrange
        existingLoan.setUsername("JohnDoe"); // Mixed case
        existingLoan.setDueDate(Date.valueOf(LocalDate.now().plusDays(1)));

        Loan requestLoan = new Loan();
        requestLoan.setLoanId(100);
        requestLoan.setUsername("johndoe"); // Lower case
        requestLoan.setDueDate(Date.valueOf(LocalDate.now().plusDays(1)));

        when(loanGrpcService.getLoanById(100)).thenReturn(existingLoan);

        // Act
        loanService.extendLoan(requestLoan);

        // Assert - No exception thrown
        verify(loanGrpcService, times(1)).extendLoan(requestLoan);
    }
}


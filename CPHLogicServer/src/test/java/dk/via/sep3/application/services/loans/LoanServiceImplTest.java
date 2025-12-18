package dk.via.sep3.application.services.loans;

import dk.via.sep3.application.domain.Book;
import dk.via.sep3.application.domain.Loan;
import dk.via.sep3.application.domain.State;
import dk.via.sep3.exceptionHandler.ResourceNotFoundException;
import dk.via.sep3.grpcConnection.bookGrpcService.BookGrpcService;
import dk.via.sep3.grpcConnection.loanGrpcService.LoanGrpcService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanServiceImplTest {

  private BookGrpcService bookGrpcService;
  private LoanGrpcService loanGrpcService;
  private LoanServiceImpl loanService;

  @BeforeEach
  void setUp() {
    bookGrpcService = mock(BookGrpcService.class);
    loanGrpcService = mock(LoanGrpcService.class);
    loanService = new LoanServiceImpl(bookGrpcService, loanGrpcService);
  }

  // ------------------------------------------------------------
  // createLoan()
  // ------------------------------------------------------------

  @Test
  void createLoan_successfulBorrow() {
    Loan request = new Loan();
    request.setUsername("john");
    request.setBookISBN("123");

    Book availableBook = new Book(1, "123", "Title", "Author", State.AVAILABLE, List.of());

    Loan persistedLoan = new Loan();
    persistedLoan.setLoanId(10);

    when(loanGrpcService.getLoansByISBN("123")).thenReturn(List.of());
    when(bookGrpcService.getBooksByIsbn("123")).thenReturn(List.of(availableBook));
    when(loanGrpcService.createLoan(any(Loan.class))).thenReturn(persistedLoan);

    Loan result = loanService.createLoan(request);

    assertEquals(10, result.getLoanId());
    verify(bookGrpcService).updateBookStatus(1, "Borrowed");
  }

  @Test
  void createLoan_duplicateActiveLoan_throwsException() {
    Loan existingLoan = new Loan();
    existingLoan.setUsername("john");
    existingLoan.setReturned(false);

    when(loanGrpcService.getLoansByISBN("123"))
        .thenReturn(List.of(existingLoan));

    Loan request = new Loan();
    request.setUsername("john");
    request.setBookISBN("123");

    assertThrows(IllegalStateException.class,
        () -> loanService.createLoan(request));
  }

  @Test
  void createLoan_noAvailableBook_throwsException() {
    Book borrowed = new Book(1, "123", "Title", "Author", State.BORROWED, List.of());

    when(loanGrpcService.getLoansByISBN("123")).thenReturn(List.of());
    when(bookGrpcService.getBooksByIsbn("123")).thenReturn(List.of(borrowed));

    Loan request = new Loan();
    request.setUsername("john");
    request.setBookISBN("123");

    assertThrows(IllegalArgumentException.class,
        () -> loanService.createLoan(request));
  }

  // ------------------------------------------------------------
  // extendLoan()
  // ------------------------------------------------------------

  @Test
  void extendLoan_successfulExtension() {
    Loan existingLoan = new Loan();
    existingLoan.setLoanId(5);
    existingLoan.setUsername("john");

    Loan request = new Loan();
    request.setLoanId(5);
    request.setUsername("john");
    request.setDueDate(Date.valueOf("2024-01-01"));
    request.setNumberOfExtensions(0);

    when(loanGrpcService.getLoanById(5)).thenReturn(existingLoan);

    loanService.extendLoan(request);

    verify(loanGrpcService).extendLoan(request);
    assertEquals(1, request.getNumberOfExtensions());
  }

  @Test
  void extendLoan_wrongUser_throwsException() {
    Loan existingLoan = new Loan();
    existingLoan.setLoanId(5);
    existingLoan.setUsername("alice");

    Loan request = new Loan();
    request.setLoanId(5);
    request.setUsername("john");
    request.setDueDate(Date.valueOf("2024-01-01"));

    when(loanGrpcService.getLoanById(5)).thenReturn(existingLoan);

    assertThrows(IllegalStateException.class,
        () -> loanService.extendLoan(request));
  }

  // ------------------------------------------------------------
  // getActiveLoansByUsername()
  // ------------------------------------------------------------

  @Test
  void getActiveLoansByUsername_returnsLoans() {
    Loan loan = new Loan();
    loan.setUsername("john");

    when(loanGrpcService.getActiveLoansByUsername("john"))
        .thenReturn(List.of(loan));

    List<Loan> result = loanService.getActiveLoansByUsername("john");

    assertEquals(1, result.size());
  }

  @Test
  void getActiveLoansByUsername_noneFound_throwsException() {
    when(loanGrpcService.getActiveLoansByUsername("john"))
        .thenReturn(List.of());

    assertThrows(ResourceNotFoundException.class,
        () -> loanService.getActiveLoansByUsername("john"));
  }
}

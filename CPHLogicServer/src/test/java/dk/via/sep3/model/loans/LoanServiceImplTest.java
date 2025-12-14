package dk.via.sep3.model.loans;

import dk.via.sep3.grpcConnection.bookGrpcService.BookGrpcService;
import dk.via.sep3.grpcConnection.loanGrpcService.LoanGrpcService;
import dk.via.sep3.model.domain.Book;
import dk.via.sep3.model.domain.Loan;
import dk.via.sep3.model.domain.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanServiceImplTest {

  private BookGrpcService bookGrpc;
  private LoanGrpcService loanGrpc;
  private LoanServiceImpl service;

  @BeforeEach
  void setup() {
    bookGrpc = mock(BookGrpcService.class);
    loanGrpc = mock(LoanGrpcService.class);
    service = new LoanServiceImpl(bookGrpc, loanGrpc);
  }


  // createLoan() TESTS


  @Test
  void createLoan_throwsIfUserAlreadyHasActiveLoan() {
    Loan existing = new Loan();
    existing.setUsername("john");
    existing.setReturned(false); // active loan

    when(loanGrpc.getLoansByISBN("123")).thenReturn(List.of(existing));

    Loan newLoan = new Loan();
    newLoan.setUsername("john");
    newLoan.setBookISBN("123");

    assertThrows(IllegalStateException.class, () -> service.createLoan(newLoan));
  }

  @Test
  void createLoan_throwsIfNoBooksFound() {
    when(loanGrpc.getLoansByISBN("123")).thenReturn(Collections.emptyList());
    when(bookGrpc.getBooksByIsbn("123")).thenReturn(Collections.emptyList());

    Loan loan = new Loan();
    loan.setUsername("john");
    loan.setBookISBN("123");

    assertThrows(IllegalArgumentException.class, () -> service.createLoan(loan));
  }

  @Test
  void createLoan_throwsIfNoAvailableBooks() {
    Book b1 = new Book("123", "T", "A", State.BORROWED, new ArrayList<>());
    when(loanGrpc.getLoansByISBN("123")).thenReturn(Collections.emptyList());
    when(bookGrpc.getBooksByIsbn("123")).thenReturn(List.of(b1));

    Loan loan = new Loan();
    loan.setUsername("john");
    loan.setBookISBN("123");

    assertThrows(IllegalArgumentException.class, () -> service.createLoan(loan));
  }

  @Test
  void createLoan_successfullyCreatesLoan() {

    Book available = new Book("123", "Title", "Author", State.AVAILABLE, new ArrayList<>());
    available.setId(10);

    // No existing loans
    when(loanGrpc.getLoansByISBN("123")).thenReturn(Collections.emptyList());
    when(bookGrpc.getBooksByIsbn("123")).thenReturn(List.of(available));

    // Persist loan response
    Loan persisted = new Loan();
    persisted.setLoanId(999); // valid ID returned from server
    persisted.setBookId(10);
    persisted.setUsername("john");
    persisted.setDueDate(Date.valueOf(java.time.LocalDate.now().plusDays(30)));


    when(loanGrpc.createLoan(any(Loan.class))).thenReturn(persisted);

    Loan input = new Loan();
    input.setUsername("john");
    input.setBookISBN("123");

    Loan result = service.createLoan(input);

    assertNotNull(result);
    assertEquals(999, result.getLoanId());
    assertEquals("john", result.getUsername());
    assertEquals(10, result.getBookId());

    // verify book status update
    verify(bookGrpc, times(1)).updateBookStatus(10, "Borrowed");
  }

  @Test
  void createLoan_throwsIfPersistenceFails() {
    Book available = new Book("123", "T", "A", State.AVAILABLE, new ArrayList<>());
    available.setId(10);

    when(loanGrpc.getLoansByISBN("123")).thenReturn(Collections.emptyList());
    when(bookGrpc.getBooksByIsbn("123")).thenReturn(List.of(available));

    // Persist fails → loanId ≤ 0
    Loan badResponse = new Loan();
    badResponse.setLoanId(0);

    when(loanGrpc.createLoan(any(Loan.class))).thenReturn(badResponse);

    Loan input = new Loan();
    input.setUsername("john");
    input.setBookISBN("123");

    assertThrows(RuntimeException.class, () -> service.createLoan(input));
  }
}

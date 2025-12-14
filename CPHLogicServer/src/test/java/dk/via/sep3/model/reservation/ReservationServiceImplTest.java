package dk.via.sep3.model.reservation;

import dk.via.sep3.controller.exceptionHandler.ResourceNotFoundException;
import dk.via.sep3.grpcConnection.bookGrpcService.BookGrpcService;
import dk.via.sep3.grpcConnection.loanGrpcService.LoanGrpcService;
import dk.via.sep3.grpcConnection.reservationGrpcService.ReservationGrpcService;
import dk.via.sep3.model.domain.Book;
import dk.via.sep3.model.domain.Loan;
import dk.via.sep3.model.domain.Reservation;
import dk.via.sep3.model.domain.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReservationServiceImplTest {

  private LoanGrpcService loanGrpc;
  private BookGrpcService bookGrpc;
  private ReservationGrpcService reservationGrpc;
  private ReservationServiceImpl service;

  @BeforeEach
  void setup() {
    loanGrpc = mock(LoanGrpcService.class);
    bookGrpc = mock(BookGrpcService.class);
    reservationGrpc = mock(ReservationGrpcService.class);
    service = new ReservationServiceImpl(loanGrpc, bookGrpc, reservationGrpc);
  }


  private Book book(String isbn, State state, int id) {
    Book b = new Book(isbn, "Title " + isbn, "Author", state, new ArrayList<>());
    b.setId(id);
    return b;
  }

  private Loan loan(String username, int loanId, Date dueDate, boolean returned, int bookId, String isbn) {
    Loan l = new Loan();
    l.setLoanId(loanId);
    l.setUsername(username);
    l.setDueDate(dueDate);
    l.setReturned(returned);
    l.setBookId(bookId);
    l.setBookISBN(isbn);
    return l;
  }

  private Reservation reservation(int id, String username, int bookId, Date date, int position) {
    return new Reservation(id, username, bookId, date, position);
  }


  @Test
  void createReservation_successfulCreatesReservationAndUpdatesBookStatus() {
    String isbn = "111";
    String username = "alice";

    // Books exist but none AVAILABLE
    Book b1 = book(isbn, State.BORROWED, 1);
    Book b2 = book(isbn, State.BORROWED, 2);

    when(bookGrpc.getBooksByIsbn(isbn)).thenReturn(List.of(b1, b2));

    // No duplicate reservations
    when(reservationGrpc.getReservationsByIsbn(isbn)).thenReturn(Collections.emptyList());

    // No unreturned loans for user
    when(loanGrpc.getLoansByISBN(isbn)).thenReturn(Collections.emptyList());

    // For b1: some loans ->
    Date dueA = Date.valueOf(LocalDate.now().plusDays(3));
    Date dueB = Date.valueOf(LocalDate.now().plusDays(7));
    Loan loanA = loan("bob", 10, dueA, false, 1, isbn);
    Loan loanB = loan("charlie", 11, dueB, false, 2, isbn);

    // loanGrpc.getLoansByISBN will be called for each book; return appropriate lists
    when(loanGrpc.getLoansByISBN(b1.getIsbn())).thenReturn(List.of(loanA));
    when(loanGrpc.getLoansByISBN(b2.getIsbn())).thenReturn(List.of(loanB));

    // Persisted reservation returned by gRPC
    Reservation persisted = new Reservation();
    persisted.setId(555);
    persisted.setUsername(username);
    persisted.setBookId(1);
    persisted.setReservationDate(Date.valueOf(LocalDate.now()));

    when(reservationGrpc.createReservation(any(Reservation.class))).thenReturn(persisted);

    // Reservation count for queue position
    when(reservationGrpc.getReservationCountByISBN(isbn)).thenReturn(2);

    // Build input reservation (only username and isbn needed)
    Reservation input = new Reservation();
    input.setUsername(username);
    input.setBookISBN(isbn);

    Reservation result = service.createReservation(input);

    assertNotNull(result);
    assertEquals(555, result.getId());
    assertEquals(username, result.getUsername());
    assertEquals(2, result.getPositionInQueue());

    // verify status update was called for the chosen book (id 1)
    verify(bookGrpc, times(1)).updateBookStatus(1, State.RESERVED.toString());

    // verify persisted reservation was created
    verify(reservationGrpc, times(1)).createReservation(any(Reservation.class));
  }



  @Test
  void createReservation_throwsWhenNoBooksFound() {
    String isbn = "nope";
    when(bookGrpc.getBooksByIsbn(isbn)).thenReturn(Collections.emptyList());

    Reservation r = new Reservation();
    r.setUsername("u");
    r.setBookISBN(isbn);

    assertThrows(IllegalArgumentException.class, () -> service.createReservation(r));
  }

  @Test
  void createReservation_throwsWhenDuplicateReservationExists() {
    String isbn = "111";
    String username = "alice";

    Book b = book(isbn, State.BORROWED, 1);
    when(bookGrpc.getBooksByIsbn(isbn)).thenReturn(List.of(b));

    Reservation existing = new Reservation();
    existing.setUsername(username);
    existing.setBookId(1);

    when(reservationGrpc.getReservationsByIsbn(isbn)).thenReturn(List.of(existing));

    Reservation input = new Reservation();
    input.setUsername(username);
    input.setBookISBN(isbn);

    assertThrows(IllegalArgumentException.class, () -> service.createReservation(input));
  }

  @Test
  void createReservation_throwsWhenAvailableCopyExists() {
    String isbn = "222";
    Book available = book(isbn, State.AVAILABLE, 3);
    when(bookGrpc.getBooksByIsbn(isbn)).thenReturn(List.of(available));

    Reservation r = new Reservation();
    r.setUsername("u");
    r.setBookISBN(isbn);

    assertThrows(IllegalArgumentException.class, () -> service.createReservation(r));
  }

  @Test
  void createReservation_throwsWhenUserHasUnreturnedLoan() {
    String isbn = "333";
    String username = "alice";

    Book b = book(isbn, State.BORROWED, 4);
    when(bookGrpc.getBooksByIsbn(isbn)).thenReturn(List.of(b));

    // user already has an unreturned loan for this ISBN
    Loan userLoan = loan(username, 20, Date.valueOf(LocalDate.now().plusDays(5)), false, 4, isbn);
    when(loanGrpc.getLoansByISBN(isbn)).thenReturn(List.of(userLoan));

    Reservation r = new Reservation();
    r.setUsername(username);
    r.setBookISBN(isbn);

    assertThrows(IllegalArgumentException.class, () -> service.createReservation(r));
  }

  @Test
  void createReservation_throwsWhenNoSuitableBookFound() {
    // Scenario: books exist but none have unreturned loans -> findBookWithEarliestDueDate will fail
    String isbn = "444";
    Book b1 = book(isbn, State.BORROWED, 5);
    Book b2 = book(isbn, State.BORROWED, 6);

    when(bookGrpc.getBooksByIsbn(isbn)).thenReturn(List.of(b1, b2));

    // For each book, loanGrpc returns loans that are all returned (or empty)
    Loan returnedLoan1 = loan("bob", 30, Date.valueOf(LocalDate.now().plusDays(1)), true, 5, isbn);
    when(loanGrpc.getLoansByISBN(b1.getIsbn())).thenReturn(List.of(returnedLoan1));
    when(loanGrpc.getLoansByISBN(b2.getIsbn())).thenReturn(Collections.emptyList());

    Reservation r = new Reservation();
    r.setUsername("u");
    r.setBookISBN(isbn);

    assertThrows(ResourceNotFoundException.class, () -> service.createReservation(r));
  }

  @Test
  void createReservation_throwsWhenPersistenceFails() {
    String isbn = "555";
    String username = "alice";

    Book b = book(isbn, State.BORROWED, 7);
    when(bookGrpc.getBooksByIsbn(isbn)).thenReturn(List.of(b));
    when(reservationGrpc.getReservationsByIsbn(isbn)).thenReturn(Collections.emptyList());
    when(loanGrpc.getLoansByISBN(isbn)).thenReturn(Collections.emptyList());

    // For book -> no unreturned loans, so findBookWithEarliestDueDate will throw ResourceNotFoundException
    // To reach persistence failure, supply a book that DOES have an unreturned loan
    Loan unreturned = loan("other", 40, Date.valueOf(LocalDate.now().plusDays(2)), false, 7, isbn);
    when(loanGrpc.getLoansByISBN(b.getIsbn())).thenReturn(List.of(unreturned));

    // Reservation persisted response is invalid (id <= 0)
    Reservation bad = new Reservation();
    bad.setId(0);
    when(reservationGrpc.createReservation(any(Reservation.class))).thenReturn(bad);

    when(reservationGrpc.getReservationCountByISBN(isbn)).thenReturn(1);

    Reservation input = new Reservation();
    input.setUsername(username);
    input.setBookISBN(isbn);

    assertThrows(RuntimeException.class, () -> service.createReservation(input));
  }
}

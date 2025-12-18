package dk.via.sep3.application.services.reservation;

import dk.via.sep3.application.domain.Book;
import dk.via.sep3.application.domain.Loan;
import dk.via.sep3.application.domain.Reservation;
import dk.via.sep3.application.domain.State;
import dk.via.sep3.exceptionHandler.ResourceNotFoundException;
import dk.via.sep3.grpcConnection.bookGrpcService.BookGrpcService;
import dk.via.sep3.grpcConnection.loanGrpcService.LoanGrpcService;
import dk.via.sep3.grpcConnection.reservationGrpcService.ReservationGrpcService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceImplTest {

  private LoanGrpcService loanGrpcService;
  private BookGrpcService bookGrpcService;
  private ReservationGrpcService reservationGrpcService;
  private ReservationServiceImpl reservationService;

  @BeforeEach
  void setUp() {
    loanGrpcService = mock(LoanGrpcService.class);
    bookGrpcService = mock(BookGrpcService.class);
    reservationGrpcService = mock(ReservationGrpcService.class);

    reservationService = new ReservationServiceImpl(
        loanGrpcService, bookGrpcService, reservationGrpcService
    );
  }

  // ------------------------------------------------------------
  // createReservation() - success
  // ------------------------------------------------------------

  @Test
  void createReservation_successful() {
    Reservation request = new Reservation();
    request.setUsername("john");
    request.setBookISBN("123");

    Book book = new Book(1, "123", "Title", "Author", State.BORROWED, List.of());

    Loan loan = new Loan();
    loan.setReturned(false);
    loan.setDueDate(Date.valueOf("2024-01-10"));

    Reservation persisted = new Reservation();
    persisted.setId(10);
    persisted.setUsername("john");
    persisted.setBookId(1);
    persisted.setReservationDate(Date.valueOf("2024-01-01"));

    when(bookGrpcService.getBooksByIsbn("123"))
        .thenReturn(List.of(book));
    when(reservationGrpcService.getReservationsByIsbn("123"))
        .thenReturn(List.of());
    when(loanGrpcService.getLoansByISBN("123"))
        .thenReturn(List.of(loan));
    when(reservationGrpcService.createReservation(any(Reservation.class)))
        .thenReturn(persisted);
    when(reservationGrpcService.getReservationCountByISBN("123"))
        .thenReturn(1);

    Reservation result = reservationService.createReservation(request);

    assertEquals(10, result.getId());
    assertEquals(1, result.getPositionInQueue());

    verify(bookGrpcService).updateBookStatus(1, "Reserved");
  }

  // ------------------------------------------------------------
  // createReservation() - failure cases
  // ------------------------------------------------------------

  @Test
  void createReservation_bookAvailable_throwsException() {
    Reservation request = new Reservation();
    request.setUsername("john");
    request.setBookISBN("123");

    Book availableBook = new Book(1, "123", "Title", "Author", State.AVAILABLE, List.of());

    when(bookGrpcService.getBooksByIsbn("123"))
        .thenReturn(List.of(availableBook));

    assertThrows(IllegalArgumentException.class,
        () -> reservationService.createReservation(request));
  }

  @Test
  void createReservation_duplicateReservation_throwsException() {
    Reservation request = new Reservation();
    request.setUsername("john");
    request.setBookISBN("123");

    Reservation existing = new Reservation();
    existing.setUsername("john");

    Book book = new Book(1, "123", "Title", "Author", State.BORROWED, List.of());

    when(bookGrpcService.getBooksByIsbn("123")).thenReturn(List.of(book));
    when(reservationGrpcService.getReservationsByIsbn("123"))
        .thenReturn(List.of(existing));

    assertThrows(IllegalArgumentException.class,
        () -> reservationService.createReservation(request));
  }

  @Test
  void createReservation_userHasUnreturnedLoan_throwsException() {
    Reservation request = new Reservation();
    request.setUsername("john");
    request.setBookISBN("123");

    Book book = new Book(1, "123", "Title", "Author", State.BORROWED, List.of());

    Loan activeLoan = new Loan();
    activeLoan.setUsername("john");
    activeLoan.setReturned(false);

    when(bookGrpcService.getBooksByIsbn("123"))
        .thenReturn(List.of(book));
    when(reservationGrpcService.getReservationsByIsbn("123"))
        .thenReturn(List.of());
    when(loanGrpcService.getLoansByISBN("123"))
        .thenReturn(List.of(activeLoan));

    assertThrows(IllegalArgumentException.class,
        () -> reservationService.createReservation(request));
  }

  @Test
  void createReservation_noSuitableBook_throwsException() {
    Reservation request = new Reservation();
    request.setUsername("john");
    request.setBookISBN("123");

    Book book = new Book(1, "123", "Title", "Author", State.BORROWED, List.of());

    Loan returnedLoan = new Loan();
    returnedLoan.setReturned(true);

    when(bookGrpcService.getBooksByIsbn("123"))
        .thenReturn(List.of(book));
    when(reservationGrpcService.getReservationsByIsbn("123"))
        .thenReturn(List.of());
    when(loanGrpcService.getLoansByISBN("123"))
        .thenReturn(List.of(returnedLoan));

    assertThrows(ResourceNotFoundException.class,
        () -> reservationService.createReservation(request));
  }
}

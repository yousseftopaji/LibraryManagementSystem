package dk.via.sep3.model.reservation;

import dk.via.sep3.grpcConnection.bookGrpcService.BookGrpcService;
import dk.via.sep3.grpcConnection.loanGrpcService.LoanGrpcService;
import dk.via.sep3.grpcConnection.reservationGrpcService.ReservationGrpcService;
import dk.via.sep3.model.domain.Book;
import dk.via.sep3.model.domain.Loan;
import dk.via.sep3.model.domain.Reservation;
import dk.via.sep3.model.domain.State;
import dk.via.sep3.model.utils.validation.Validator;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.time.LocalDate;

@Service public class ReservationServiceImpl implements ReservationService
{
  private final Validator validator;
  private final LoanGrpcService loanGrpcService;
  private final BookGrpcService bookGrpcService;
  private final ReservationGrpcService reservationGrpcService;

  public ReservationServiceImpl(Validator validator,
                                LoanGrpcService loanGrpcService, BookGrpcService bookGrpcService,
                                ReservationGrpcService reservationGrpcService)
  {
    this.validator = validator;
    this.loanGrpcService = loanGrpcService;
    this.bookGrpcService = bookGrpcService;
    this.reservationGrpcService = reservationGrpcService;
  }

  @Override public Reservation createReservation(Reservation reservation)
  {
    String username = reservation.getUsername();
    String isbn = reservation.getBookISBN();

    // -----------------------------
    // Step 1 — Validate user
    // -----------------------------
    validator.validate(username);

    // -----------------------------
    // Step 2 — Check ISBN availability
    // -----------------------------

    List<Book> books = bookGrpcService.getBooksByIsbn(isbn);
    if (books.isEmpty())
    {
      throw new IllegalArgumentException("No books found with ISBN: " + isbn);
    }

    // -----------------------------
    // Check if user already has an active reservation for this book
    // -----------------------------
    List<Reservation> existingReservations = reservationGrpcService.getReservationsByIsbn(
        isbn);

    for (Reservation existingReservation : existingReservations)
    {
      System.out.println("Existing reservation by user: " + existingReservation.getUsername() + " for book Id: " + existingReservation.getBookId() + " and ISBN: " + isbn);
      if (existingReservation.getUsername().equalsIgnoreCase(username))
      {
        throw new IllegalArgumentException(
            "User already has an active reservation for this book.");
      }
    }

    // -----------------------------
    // Step 3 — Check if any copies are available
    // -----------------------------
    boolean anyAvailable = books.stream().anyMatch(
        book -> book.getState().toString().equalsIgnoreCase("AVAILABLE"));
    if (anyAvailable)
    {
      throw new IllegalArgumentException(
          "Book is currently available. Cannot reserve, but borrow instead.");
    }

    // -----------------------------
    // Step 4 — Check if the user already has an unreturned loan for this book
    // -----------------------------

    List<Loan> userLoans = loanGrpcService.getLoansByISBN(isbn);
    for (Loan loan : userLoans)
    {
      if (loan.getUsername().equalsIgnoreCase(username) && !loan.isReturned())
      {
        throw new IllegalArgumentException(
            "User already has an unreturned loan for this book.");
      }
    }

    // -----------------------------
    // Find the book with the earliest due date, and that is not reserved by any other user
    // -----------------------------
    Book targetBook = null;
    Date earliestDueDate = null;
    for (Book book : books)
    {
      if (!book.getState().toString().equalsIgnoreCase("RESERVED"))
      {
        List<Loan> loansForBook = loanGrpcService.getLoansByISBN(
            book.getIsbn());
        for (Loan loan : loansForBook)
        {
          if (!loan.isReturned())
          {
            if (earliestDueDate == null || loan.getDueDate()
                .before(earliestDueDate))
            {
              earliestDueDate = loan.getDueDate();
              targetBook = book;
            }
          }
        }
      }
    }

    if (targetBook == null)
    {
      throw new RuntimeException(
          "No suitable book found for reservation with ISBN: " + isbn);
    }

    // -----------------------------
    // Step 5 — Create reservation
    // -----------------------------

    Date reservationDate = Date.valueOf(LocalDate.now());
    Reservation createReservation = new Reservation();
    createReservation.setUsername(username);
    createReservation.setBookId(targetBook.getId());
    createReservation.setReservationDate(reservationDate);

    Reservation grpcReservation = reservationGrpcService.createReservation(
        createReservation);
    if (grpcReservation == null || grpcReservation.getId() <= 0)
    {
      throw new RuntimeException(
          "Failed to create reservation - received invalid response from server");
    }

    bookGrpcService.updateBookStatus(targetBook.getId(),
        State.RESERVED.toString());

    int countReservations = reservationGrpcService.getReservationCountByISBN(
        isbn);

    return new Reservation(grpcReservation.getId(),
        grpcReservation.getUsername(), grpcReservation.getBookId(),
        grpcReservation.getReservationDate(), countReservations);
  }
}
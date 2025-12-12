package dk.via.sep3.model.reservation;

import dk.via.sep3.exceptionHandler.ResourceNotFoundException;
import dk.via.sep3.grpcConnection.bookGrpcService.BookGrpcService;
import dk.via.sep3.grpcConnection.loanGrpcService.LoanGrpcService;
import dk.via.sep3.grpcConnection.reservationGrpcService.ReservationGrpcService;
import dk.via.sep3.model.domain.Book;
import dk.via.sep3.model.domain.Loan;
import dk.via.sep3.model.domain.Reservation;
import dk.via.sep3.model.domain.State;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.time.LocalDate;

@Service
public class ReservationServiceImpl implements ReservationService {
    private final LoanGrpcService loanGrpcService;
    private final BookGrpcService bookGrpcService;
    private final ReservationGrpcService reservationGrpcService;

    public ReservationServiceImpl(LoanGrpcService loanGrpcService, BookGrpcService bookGrpcService,
                                  ReservationGrpcService reservationGrpcService) {
        this.loanGrpcService = loanGrpcService;
        this.bookGrpcService = bookGrpcService;
        this.reservationGrpcService = reservationGrpcService;
    }

    @Override
    public Reservation createReservation(Reservation reservation) {
        String username = reservation.getUsername();
        String isbn = reservation.getBookISBN();

        // Step 1: Retrieve and validate books exist
        List<Book> books = retrieveAndValidateBooksExist(isbn);

        // Step 2: Validate no duplicate reservation
        validateNoDuplicateReservation(username, isbn);

        // Step 3: Validate no available copies (must be borrowed to reserve)
        validateNoAvailableCopies(books);

        // Step 4: Validate user doesn't have unreturned loan
        validateNoUnreturnedLoan(username, isbn);

        // Step 5: Find best book to reserve (earliest due date)
        Book targetBook = findBookWithEarliestDueDate(books, isbn);

        // Step 6: Create and persist reservation
        Reservation createdReservation = createAndPersistReservation(username, targetBook);

        // Step 7: Update book status to reserved
        updateBookStatusToReserved(targetBook.getId());

        // Step 8: Get reservation count and return complete reservation
        return buildCompleteReservation(createdReservation, isbn);
    }

    // ==================== Validation Methods ====================

    /**
     * Retrieves books by ISBN and validates that at least one exists.
     */
    private List<Book> retrieveAndValidateBooksExist(String isbn) {
        List<Book> books = bookGrpcService.getBooksByIsbn(isbn);
        if (books.isEmpty()) {
            throw new IllegalArgumentException("No books found with ISBN: " + isbn);
        }
        return books;
    }

    /**
     * Validates that the user doesn't already have an active reservation for this ISBN.
     */
    private void validateNoDuplicateReservation(String username, String isbn) {
        List<Reservation> existingReservations = reservationGrpcService.getReservationsByIsbn(isbn);

        for (Reservation existingReservation : existingReservations) {
            if (existingReservation.getUsername().equalsIgnoreCase(username)) {
                throw new IllegalArgumentException(
                        "User already has an active reservation for this book.");
            }
        }
    }

    /**
     * Validates that no copies are available. Users must borrow available books, not reserve them.
     */
    private void validateNoAvailableCopies(List<Book> books) {
        boolean anyAvailable = books.stream()
                .anyMatch(book -> book.getState().toString().equalsIgnoreCase("AVAILABLE"));

        if (anyAvailable) {
            throw new IllegalArgumentException(
                    "Book is currently available. Cannot reserve, but borrow instead.");
        }
    }

    /**
     * Validates that the user doesn't have an unreturned loan for this book.
     */
    private void validateNoUnreturnedLoan(String username, String isbn) {
        List<Loan> userLoans = loanGrpcService.getLoansByISBN(isbn);

        for (Loan loan : userLoans) {
            if (loan.getUsername().equalsIgnoreCase(username) && !loan.isReturned()) {
                throw new IllegalArgumentException(
                        "User already has an unreturned loan for this book.");
            }
        }
    }

    // ==================== Book Selection Methods ====================

    /**
     * Finds the book with the earliest due date that is not already reserved.
     * This ensures the user gets notified as soon as possible when a copy becomes available.
     */
    private Book findBookWithEarliestDueDate(List<Book> books, String isbn) {
        Book targetBook = null;
        Date earliestDueDate = null;

        for (Book book : books) {
            Date bookEarliestDueDate = findEarliestDueDateForBook(book);

            if (bookEarliestDueDate != null &&
                    (earliestDueDate == null || bookEarliestDueDate.before(earliestDueDate))) {
                earliestDueDate = bookEarliestDueDate;
                targetBook = book;
            }
        }

        if (targetBook == null) {
            throw new ResourceNotFoundException(
                    "No suitable book found for reservation with ISBN: " + isbn);
        }

        return targetBook;
    }

    /**
     * Finds the earliest due date among all unreturned loans for a specific book.
     */
    private Date findEarliestDueDateForBook(Book book) {
        List<Loan> loansForBook = loanGrpcService.getLoansByISBN(book.getIsbn());
        Date earliestDueDate = null;

        for (Loan loan : loansForBook) {
            if (!loan.isReturned()) {
                if (earliestDueDate == null || loan.getDueDate().before(earliestDueDate)) {
                    earliestDueDate = loan.getDueDate();
                }
            }
        }

        return earliestDueDate;
    }

    // ==================== Reservation Creation Methods ====================

    /**
     * Creates a reservation object and persists it via gRPC.
     */
    private Reservation createAndPersistReservation(String username, Book targetBook) {
        Reservation reservation = createReservationObject(username, targetBook);
        Reservation persistedReservation = reservationGrpcService.createReservation(reservation);

        validateReservationPersistence(persistedReservation);

        return persistedReservation;
    }

    /**
     * Creates a new reservation domain object.
     */
    private Reservation createReservationObject(String username, Book targetBook) {
        Date reservationDate = Date.valueOf(LocalDate.now());

        Reservation reservation = new Reservation();
        reservation.setUsername(username);
        reservation.setBookId(targetBook.getId());
        reservation.setReservationDate(reservationDate);

        return reservation;
    }

    /**
     * Validates that the reservation was successfully persisted.
     */
    private void validateReservationPersistence(Reservation reservation) {
        if (reservation == null || reservation.getId() <= 0) {
            throw new RuntimeException(
                    "Failed to create reservation - received invalid response from server");
        }
    }

    /**
     * Updates the book status to RESERVED.
     */
    private void updateBookStatusToReserved(long bookId) {
        bookGrpcService.updateBookStatus((int) bookId, State.RESERVED.toString());
    }

    /**
     * Builds the complete reservation with queue position count.
     */
    private Reservation buildCompleteReservation(Reservation reservation, String isbn) {
        int queuePosition = reservationGrpcService.getReservationCountByISBN(isbn);

        return new Reservation(
                reservation.getId(),
                reservation.getUsername(),
                reservation.getBookId(),
                reservation.getReservationDate(),
                queuePosition
        );
    }
}

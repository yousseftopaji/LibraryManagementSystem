package dk.via.sep3.model.reservation;

import dk.via.sep3.DTOBook;
import dk.via.sep3.DTOLoan;
import dk.via.sep3.DTOReservation;

import dk.via.sep3.grpcConnection.bookPersistenceService.BookPersistenceService;
import dk.via.sep3.grpcConnection.loanPersistenceService.LoanPersistenceService;
import dk.via.sep3.grpcConnection.reservationPersistenceService.ReservationPersistenceService;
import dk.via.sep3.model.utils.validation.Validator;
import dk.via.sep3.shared.reservation.CreateReservationDTO;
import dk.via.sep3.shared.reservation.ReservationDTO;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Comparator;
import java.util.List;
import java.time.LocalDate;

@Service
public class ReservationServiceImpl implements dk.via.sep3.model.reservation.ReservationService {
    private final Validator validator;
    private final LoanPersistenceService loanPersistenceService;
    private final BookPersistenceService bookPersistenceService;
    private final ReservationPersistenceService reservationPersistenceService;

    public ReservationServiceImpl(Validator validator,
                                  LoanPersistenceService loanPersistenceService,
                                  BookPersistenceService bookPersistenceService,
                                  ReservationPersistenceService reservationPersistenceService) {
        this.validator = validator;
        this.loanPersistenceService = loanPersistenceService;
        this.bookPersistenceService = bookPersistenceService;
        this.reservationPersistenceService = reservationPersistenceService;
    }

    @Override
    public synchronized ReservationDTO createReservation(
            CreateReservationDTO dto) {
        String username = dto.getUsername();
        String isbn = dto.getBookISBN();

        // -----------------------------
        // Step 1 — Validate user
        // -----------------------------
        validator.validateUser(username);

        // -----------------------------
        // Step 2 — Check ISBN availability
        // -----------------------------

        List<DTOBook> books = bookPersistenceService.getBooksByIsbn(isbn);
        if (books.isEmpty()) {
            throw new IllegalArgumentException("No books found with ISBN: " + isbn);
        }

        // -----------------------------
        // Step 3 — Check if any copies are available
        // -----------------------------
        boolean anyAvailable = books.stream()
                .anyMatch(book -> book.getState().equalsIgnoreCase("AVAILABLE"));
        if (anyAvailable) {
            throw new IllegalArgumentException(
                    "Book is currently available. Cannot reserve, but borrow instead.");
        }

        // -----------------------------
        // Step 4 — Check if the user already has an unreturned loan for this book
        // -----------------------------

        List<DTOLoan> userLoans = loanPersistenceService.getLoansByISBN(isbn);
        for (DTOLoan loan : userLoans) {
            if (loan.getUsername().equalsIgnoreCase(username)
                    && !loan.getIsReturned()) {
                throw new IllegalArgumentException(
                        "User already has an unreturned loan for this book.");
            }
        }

        // -----------------------------
        // Find the book with the earliest due date
        // -----------------------------

        DTOBook targetBook = null;
        Date earliestDueDate = null;
        for (DTOLoan loan : userLoans) {
            Date dueDate = Date.valueOf(loan.getDueDate());
            if (earliestDueDate == null || dueDate.before(earliestDueDate)) {
                earliestDueDate = dueDate;
                targetBook = books.stream()
                        .filter(book -> book.getId() == loan.getBookId())
                        .findFirst()
                        .orElse(null);
            }
        }
        if (targetBook == null) {
            throw new IllegalArgumentException("No valid book found to reserve.");
        }

        // -----------------------------
        // Step 5 — Create reservation
        // -----------------------------

        Date reservationDate = Date.valueOf(LocalDate.now());
        DTOReservation grpcReservation = reservationPersistenceService.createReservation(
                username, String.valueOf(targetBook.getId()),
                reservationDate.toString());
        if (grpcReservation == null || grpcReservation.getId() <= 0) {
            throw new RuntimeException(
                    "Failed to create reservation - received invalid response from server");
        }

        int countReservations = reservationPersistenceService.getReservationCountByISBN(
                isbn);

        return new ReservationDTO(grpcReservation.getId(),
                grpcReservation.getReservationDate(), grpcReservation.getBookId(),
                Date.valueOf(grpcReservation.getReservationDate()), countReservations);
    }
}
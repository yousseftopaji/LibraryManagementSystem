package dk.via.sep3.model.reservation;

import dk.via.sep3.exceptionHandler.ResourceNotFoundException;
import dk.via.sep3.grpcConnection.bookGrpcService.BookGrpcService;
import dk.via.sep3.grpcConnection.loanGrpcService.LoanGrpcService;
import dk.via.sep3.grpcConnection.reservationGrpcService.ReservationGrpcService;
import dk.via.sep3.model.domain.Book;
import dk.via.sep3.model.domain.Loan;
import dk.via.sep3.model.domain.Reservation;
import dk.via.sep3.model.domain.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private LoanGrpcService loanGrpcService;

    @Mock
    private BookGrpcService bookGrpcService;

    @Mock
    private ReservationGrpcService reservationGrpcService;

    private ReservationServiceImpl reservationService;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationServiceImpl(loanGrpcService, bookGrpcService, reservationGrpcService);
    }

    @Test
    @DisplayName("Should create reservation successfully")
    void testCreateReservation_Success() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setUsername("testuser");
        reservation.setBookISBN("123456");

        Book book = new Book();
        book.setId(1);
        book.setIsbn("123456");
        book.setState(State.BORROWED);

        Loan loan = new Loan();
        loan.setLoanId(1);
        loan.setDueDate(Date.valueOf("2025-02-01"));
        loan.setReturned(false);

        Reservation createdReservation = new Reservation();
        createdReservation.setId(1);
        createdReservation.setUsername("testuser");
        createdReservation.setBookId(1);

        when(bookGrpcService.getBooksByIsbn("123456")).thenReturn(List.of(book));
        when(reservationGrpcService.getReservationsByIsbn("123456")).thenReturn(new ArrayList<>());
        when(loanGrpcService.getLoansByISBN("123456")).thenReturn(List.of(loan));
        when(reservationGrpcService.createReservation(any(Reservation.class))).thenReturn(createdReservation);
        when(reservationGrpcService.getReservationCountByISBN("123456")).thenReturn(1);

        // Act
        Reservation result = reservationService.createReservation(reservation);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(bookGrpcService).updateBookStatus(1, "Reserved");
        verify(reservationGrpcService).createReservation(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw exception when no books found")
    void testCreateReservation_NoBooksFound() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setBookISBN("999999");

        when(bookGrpcService.getBooksByIsbn("999999")).thenReturn(new ArrayList<>());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reservationService.createReservation(reservation)
        );
        assertEquals("No books found with ISBN: 999999", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when user already has reservation")
    void testCreateReservation_DuplicateReservation() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setUsername("testuser");
        reservation.setBookISBN("123456");

        Book book = new Book();
        book.setIsbn("123456");
        book.setState(State.BORROWED);

        Reservation existingReservation = new Reservation();
        existingReservation.setUsername("testuser");

        when(bookGrpcService.getBooksByIsbn("123456")).thenReturn(List.of(book));
        when(reservationGrpcService.getReservationsByIsbn("123456")).thenReturn(List.of(existingReservation));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reservationService.createReservation(reservation)
        );
        assertEquals("User already has an active reservation for this book.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when book is available")
    void testCreateReservation_BookAvailable() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setUsername("testuser");
        reservation.setBookISBN("123456");

        Book book = new Book();
        book.setIsbn("123456");
        book.setState(State.AVAILABLE);

        when(bookGrpcService.getBooksByIsbn("123456")).thenReturn(List.of(book));
        when(reservationGrpcService.getReservationsByIsbn("123456")).thenReturn(new ArrayList<>());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reservationService.createReservation(reservation)
        );
        assertEquals("Book is currently available. Cannot reserve, but borrow instead.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when user has unreturned loan")
    void testCreateReservation_UnreturnedLoan() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setUsername("testuser");
        reservation.setBookISBN("123456");

        Book book = new Book();
        book.setIsbn("123456");
        book.setState(State.BORROWED);

        Loan unreturnedLoan = new Loan();
        unreturnedLoan.setUsername("testuser");
        unreturnedLoan.setReturned(false);

        when(bookGrpcService.getBooksByIsbn("123456")).thenReturn(List.of(book));
        when(reservationGrpcService.getReservationsByIsbn("123456")).thenReturn(new ArrayList<>());
        when(loanGrpcService.getLoansByISBN("123456")).thenReturn(List.of(unreturnedLoan));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reservationService.createReservation(reservation)
        );
        assertEquals("User already has an unreturned loan for this book.", exception.getMessage());
    }

    @Test
    @DisplayName("Should select book with earliest due date")
    void testCreateReservation_SelectsEarliestDueDate() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setUsername("testuser");
        reservation.setBookISBN("123456");

        Book book1 = new Book();
        book1.setId(1);
        book1.setIsbn("123456");
        book1.setState(State.BORROWED);

        Book book2 = new Book();
        book2.setId(2);
        book2.setIsbn("123456");
        book2.setState(State.BORROWED);

        Loan loan1 = new Loan();
        loan1.setBookId(1);
        loan1.setDueDate(Date.valueOf("2025-03-01"));
        loan1.setReturned(false);

        Loan loan2 = new Loan();
        loan2.setBookId(2);
        loan2.setDueDate(Date.valueOf("2025-02-01")); // Earlier
        loan2.setReturned(false);

        Reservation createdReservation = new Reservation();
        createdReservation.setId(1);
        createdReservation.setBookId(2); // Should select book2

        when(bookGrpcService.getBooksByIsbn("123456")).thenReturn(List.of(book1, book2));
        when(reservationGrpcService.getReservationsByIsbn("123456")).thenReturn(new ArrayList<>());
        when(loanGrpcService.getLoansByISBN("123456")).thenReturn(List.of(loan1, loan2));
        when(reservationGrpcService.createReservation(any(Reservation.class))).thenReturn(createdReservation);
        when(reservationGrpcService.getReservationCountByISBN("123456")).thenReturn(1);

        // Act
        Reservation result = reservationService.createReservation(reservation);

        // Assert
        assertNotNull(result);
        verify(bookGrpcService).updateBookStatus(2, "Reserved");
    }

    @Test
    @DisplayName("Should throw exception when reservation persistence fails")
    void testCreateReservation_PersistenceFails() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setUsername("testuser");
        reservation.setBookISBN("123456");

        Book book = new Book();
        book.setId(1);
        book.setIsbn("123456");
        book.setState(State.BORROWED);

        Loan loan = new Loan();
        loan.setDueDate(Date.valueOf("2025-02-01"));
        loan.setReturned(false);

        Reservation createdReservation = new Reservation();
        createdReservation.setId(0); // Invalid ID

        when(bookGrpcService.getBooksByIsbn("123456")).thenReturn(List.of(book));
        when(reservationGrpcService.getReservationsByIsbn("123456")).thenReturn(new ArrayList<>());
        when(loanGrpcService.getLoansByISBN("123456")).thenReturn(List.of(loan));
        when(reservationGrpcService.createReservation(any(Reservation.class))).thenReturn(createdReservation);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> reservationService.createReservation(reservation));
    }

    @Test
    @DisplayName("Should include position in queue in response")
    void testCreateReservation_IncludesQueuePosition() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setUsername("testuser");
        reservation.setBookISBN("123456");

        Book book = new Book();
        book.setId(1);
        book.setIsbn("123456");
        book.setState(State.BORROWED);

        Loan loan = new Loan();
        loan.setDueDate(Date.valueOf("2025-02-01"));
        loan.setReturned(false);

        Reservation createdReservation = new Reservation();
        createdReservation.setId(1);
        createdReservation.setBookId(1);

        when(bookGrpcService.getBooksByIsbn("123456")).thenReturn(List.of(book));
        when(reservationGrpcService.getReservationsByIsbn("123456")).thenReturn(new ArrayList<>());
        when(loanGrpcService.getLoansByISBN("123456")).thenReturn(List.of(loan));
        when(reservationGrpcService.createReservation(any(Reservation.class))).thenReturn(createdReservation);
        when(reservationGrpcService.getReservationCountByISBN("123456")).thenReturn(3);

        // Act
        Reservation result = reservationService.createReservation(reservation);

        // Assert
        assertEquals(3, result.getPositionInQueue());
    }

    @Test
    @DisplayName("Should handle case-insensitive username comparison for duplicate check")
    void testCreateReservation_CaseInsensitiveUsername() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setUsername("TestUser");
        reservation.setBookISBN("123456");

        Book book = new Book();
        book.setIsbn("123456");
        book.setState(State.BORROWED);

        Reservation existingReservation = new Reservation();
        existingReservation.setUsername("testuser"); // Different case

        when(bookGrpcService.getBooksByIsbn("123456")).thenReturn(List.of(book));
        when(reservationGrpcService.getReservationsByIsbn("123456")).thenReturn(List.of(existingReservation));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> reservationService.createReservation(reservation));
    }

    @Test
    @DisplayName("Should ignore returned loans when finding earliest due date")
    void testCreateReservation_IgnoresReturnedLoans() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setUsername("testuser");
        reservation.setBookISBN("123456");

        Book book = new Book();
        book.setId(1);
        book.setIsbn("123456");
        book.setState(State.BORROWED);

        Loan returnedLoan = new Loan();
        returnedLoan.setBookId(1);
        returnedLoan.setDueDate(Date.valueOf("2025-01-01")); // Very early but returned
        returnedLoan.setReturned(true);

        Loan activeLoans = new Loan();
        activeLoans.setBookId(1);
        activeLoans.setDueDate(Date.valueOf("2025-03-01"));
        activeLoans.setReturned(false);

        Reservation createdReservation = new Reservation();
        createdReservation.setId(1);
        createdReservation.setBookId(1);

        when(bookGrpcService.getBooksByIsbn("123456")).thenReturn(List.of(book));
        when(reservationGrpcService.getReservationsByIsbn("123456")).thenReturn(new ArrayList<>());
        when(loanGrpcService.getLoansByISBN("123456")).thenReturn(List.of(returnedLoan, activeLoans));
        when(reservationGrpcService.createReservation(any(Reservation.class))).thenReturn(createdReservation);
        when(reservationGrpcService.getReservationCountByISBN("123456")).thenReturn(1);

        // Act
        Reservation result = reservationService.createReservation(reservation);

        // Assert
        assertNotNull(result);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when no suitable book found")
    void testCreateReservation_NoSuitableBook() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setUsername("testuser");
        reservation.setBookISBN("123456");

        Book book = new Book();
        book.setId(1);
        book.setIsbn("123456");
        book.setState(State.BORROWED);

        // All loans are returned, so no active loan exists
        when(bookGrpcService.getBooksByIsbn("123456")).thenReturn(List.of(book));
        when(reservationGrpcService.getReservationsByIsbn("123456")).thenReturn(new ArrayList<>());
        when(loanGrpcService.getLoansByISBN("123456")).thenReturn(new ArrayList<>());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> reservationService.createReservation(reservation));
    }
}


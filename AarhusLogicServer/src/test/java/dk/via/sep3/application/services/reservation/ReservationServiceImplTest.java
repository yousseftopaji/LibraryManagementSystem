package dk.via.sep3.application.services.reservation;

import dk.via.sep3.exceptionHandler.ResourceNotFoundException;
import dk.via.sep3.grpcConnection.bookGrpcService.BookGrpcService;
import dk.via.sep3.grpcConnection.loanGrpcService.LoanGrpcService;
import dk.via.sep3.grpcConnection.reservationGrpcService.ReservationGrpcService;
import dk.via.sep3.application.domain.Book;
import dk.via.sep3.application.domain.Loan;
import dk.via.sep3.application.domain.Reservation;
import dk.via.sep3.application.domain.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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


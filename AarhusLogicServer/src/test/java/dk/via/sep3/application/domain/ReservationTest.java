package dk.via.sep3.application.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Reservation domain class
 * Tests domain object construction and field access
 */
class ReservationTest {

    private Reservation reservation;
    private Date reservationDate;

    @BeforeEach
    void setUp() {
        reservation = new Reservation();
        reservationDate = Date.valueOf(LocalDate.now());
    }

    @Test
    @DisplayName("Should create Reservation with no-arg constructor")
    void testNoArgConstructor() {
        // Act
        Reservation r = new Reservation();

        // Assert
        assertNotNull(r);
        assertEquals(0, r.getId());
        assertEquals(0, r.getBookId());
        assertNull(r.getUsername());
        assertNull(r.getReservationDate());
    }

    @Test
    @DisplayName("Should create Reservation with all args constructor")
    void testAllArgsConstructor() {
        // Act
        Reservation r = new Reservation(1, "johndoe", 100, reservationDate, 1);

        // Assert
        assertEquals(1, r.getId());
        assertEquals("johndoe", r.getUsername());
        assertEquals(100, r.getBookId());
        assertEquals(reservationDate, r.getReservationDate());
        assertEquals(1, r.getPositionInQueue());
    }

    @Test
    @DisplayName("Should set and get id")
    void testSetGetId() {
        // Act
        reservation.setId(42);

        // Assert
        assertEquals(42, reservation.getId());
    }

    @Test
    @DisplayName("Should set and get book id")
    void testSetGetBookId() {
        // Act
        reservation.setBookId(123);

        // Assert
        assertEquals(123, reservation.getBookId());
    }

    @Test
    @DisplayName("Should set and get username")
    void testSetGetUsername() {
        // Act
        reservation.setUsername("janedoe");

        // Assert
        assertEquals("janedoe", reservation.getUsername());
    }

    @Test
    @DisplayName("Should set and get reservation date")
    void testSetGetReservationDate() {
        // Act
        reservation.setReservationDate(reservationDate);

        // Assert
        assertEquals(reservationDate, reservation.getReservationDate());
    }

    @Test
    @DisplayName("Should handle null username")
    void testNullUsername() {
        // Act
        reservation.setUsername(null);

        // Assert
        assertNull(reservation.getUsername());
    }

    @Test
    @DisplayName("Should handle null date")
    void testNullDate() {
        // Act
        reservation.setReservationDate(null);

        // Assert
        assertNull(reservation.getReservationDate());
    }

    @Test
    @DisplayName("Should handle empty username")
    void testEmptyUsername() {
        // Act
        reservation.setUsername("");

        // Assert
        assertEquals("", reservation.getUsername());
    }

    @Test
    @DisplayName("Should handle negative id")
    void testNegativeId() {
        // Act
        reservation.setId(-1);

        // Assert
        assertEquals(-1, reservation.getId());
    }

    @Test
    @DisplayName("Should handle negative book id")
    void testNegativeBookId() {
        // Act
        reservation.setBookId(-1);

        // Assert
        assertEquals(-1, reservation.getBookId());
    }

    @Test
    @DisplayName("Should handle large ids")
    void testLargeIds() {
        // Act
        reservation.setId(Integer.MAX_VALUE);
        reservation.setBookId(Integer.MAX_VALUE);

        // Assert
        assertEquals(Integer.MAX_VALUE, reservation.getId());
        assertEquals(Integer.MAX_VALUE, reservation.getBookId());
    }

    @Test
    @DisplayName("Should handle date in past")
    void testDateInPast() {
        // Arrange
        Date pastDate = Date.valueOf(LocalDate.now().minusDays(10));

        // Act
        reservation.setReservationDate(pastDate);

        // Assert
        assertEquals(pastDate, reservation.getReservationDate());
    }

    @Test
    @DisplayName("Should handle date in future")
    void testDateInFuture() {
        // Arrange
        Date futureDate = Date.valueOf(LocalDate.now().plusDays(10));

        // Act
        reservation.setReservationDate(futureDate);

        // Assert
        assertEquals(futureDate, reservation.getReservationDate());
    }

    @Test
    @DisplayName("Should preserve all fields in constructor")
    void testConstructor_PreservesAllFields() {
        // Act
        Reservation r = new Reservation(99, "testuser", 88, reservationDate, 3);

        // Assert
        assertEquals(99, r.getId());
        assertEquals("testuser", r.getUsername());
        assertEquals(88, r.getBookId());
        assertEquals(reservationDate, r.getReservationDate());
        assertEquals(3, r.getPositionInQueue());
    }

    @Test
    @DisplayName("Should handle special characters in username")
    void testSpecialCharactersInUsername() {
        // Act
        reservation.setUsername("user@example.com");

        // Assert
        assertEquals("user@example.com", reservation.getUsername());
    }

    @Test
    @DisplayName("Should handle zero ids")
    void testZeroIds() {
        // Act
        reservation.setId(0);
        reservation.setBookId(0);

        // Assert
        assertEquals(0, reservation.getId());
        assertEquals(0, reservation.getBookId());
    }

    @Test
    @DisplayName("Should allow field updates")
    void testFieldUpdates() {
        // Arrange
        reservation.setId(1);
        reservation.setBookId(100);
        reservation.setUsername("user1");

        // Act
        reservation.setId(2);
        reservation.setBookId(200);
        reservation.setUsername("user2");

        // Assert
        assertEquals(2, reservation.getId());
        assertEquals(200, reservation.getBookId());
        assertEquals("user2", reservation.getUsername());
    }

    @Test
    @DisplayName("Should handle today's date")
    void testTodayDate() {
        // Arrange
        Date today = Date.valueOf(LocalDate.now());

        // Act
        reservation.setReservationDate(today);

        // Assert
        assertEquals(today, reservation.getReservationDate());
    }
}


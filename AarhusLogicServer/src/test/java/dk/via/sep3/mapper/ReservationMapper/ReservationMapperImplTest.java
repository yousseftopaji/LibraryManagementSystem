package dk.via.sep3.mapper.ReservationMapper;

import dk.via.sep3.DTOReservation;
import dk.via.sep3.model.domain.Reservation;
import dk.via.sep3.shared.reservation.CreateReservationDTO;
import dk.via.sep3.shared.reservation.ReservationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class ReservationMapperImplTest {

    private ReservationMapperImpl reservationMapper;

    @BeforeEach
    void setUp() {
        reservationMapper = new ReservationMapperImpl();
    }

    @Test
    @DisplayName("Should map CreateReservationDTO to Reservation domain")
    void testMapCreateReservationDTOToDomain() {
        // Arrange
        CreateReservationDTO createReservationDTO = new CreateReservationDTO("testuser", "123456");

        // Act
        Reservation result = reservationMapper.mapCreateReservationDTOToDomain(createReservationDTO);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("123456", result.getBookISBN());
    }

    @Test
    @DisplayName("Should map Reservation domain to DTOReservation")
    void testMapDomainToDTOReservation() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1);
        reservation.setBookId(100);
        reservation.setUsername("testuser");
        reservation.setReservationDate(Date.valueOf("2025-01-01"));

        // Act
        DTOReservation result = reservationMapper.mapDomainToDTOReservation(reservation);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(100, result.getBookId());
        assertEquals("testuser", result.getUsername());
        assertEquals("2025-01-01", result.getReservationDate());
    }

    @Test
    @DisplayName("Should map DTOReservation to Reservation domain")
    void testMapDTOReservationToDomain() {
        // Arrange
        DTOReservation dtoReservation = DTOReservation.newBuilder()
            .setId(1)
            .setBookId(100)
            .setUsername("testuser")
            .setReservationDate("2025-01-01")
            .build();

        // Act
        Reservation result = reservationMapper.mapDTOReservationToDomain(dtoReservation);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(100, result.getBookId());
        assertEquals("testuser", result.getUsername());
        assertEquals(Date.valueOf("2025-01-01"), result.getReservationDate());
    }

    @Test
    @DisplayName("Should map Reservation domain to ReservationDTO")
    void testMapDomainToReservationDTO() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1);
        reservation.setUsername("testuser");
        reservation.setBookId(100);
        reservation.setReservationDate(Date.valueOf("2025-01-01"));
        reservation.setPositionInQueue(3);

        // Act
        ReservationDTO result = reservationMapper.mapDomainToReservationDTO(reservation);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals(100, result.getBookId());
        assertEquals(Date.valueOf("2025-01-01"), result.getReservationDate());
        assertEquals(3, result.getPositionInQueue());
    }

    @Test
    @DisplayName("Should handle different usernames")
    void testMapCreateReservationDTOToDomain_DifferentUsernames() {
        // Test email as username
        CreateReservationDTO dto1 = new CreateReservationDTO("user@example.com", "123");
        Reservation result1 = reservationMapper.mapCreateReservationDTOToDomain(dto1);
        assertEquals("user@example.com", result1.getUsername());

        // Test special characters
        CreateReservationDTO dto2 = new CreateReservationDTO("user_123", "456");
        Reservation result2 = reservationMapper.mapCreateReservationDTOToDomain(dto2);
        assertEquals("user_123", result2.getUsername());
    }

    @Test
    @DisplayName("Should handle different ISBNs")
    void testMapCreateReservationDTOToDomain_DifferentISBNs() {
        // Test ISBN-10
        CreateReservationDTO dto1 = new CreateReservationDTO("user", "1234567890");
        Reservation result1 = reservationMapper.mapCreateReservationDTOToDomain(dto1);
        assertEquals("1234567890", result1.getBookISBN());

        // Test ISBN-13
        CreateReservationDTO dto2 = new CreateReservationDTO("user", "978-0-123456-78-9");
        Reservation result2 = reservationMapper.mapCreateReservationDTOToDomain(dto2);
        assertEquals("978-0-123456-78-9", result2.getBookISBN());
    }

    @Test
    @DisplayName("Should preserve all fields in round-trip conversion")
    void testRoundTripConversion() {
        // Arrange
        Reservation original = new Reservation();
        original.setId(99);
        original.setBookId(999);
        original.setUsername("roundtripuser");
        original.setReservationDate(Date.valueOf("2025-06-15"));

        // Act
        DTOReservation dtoReservation = reservationMapper.mapDomainToDTOReservation(original);
        Reservation converted = reservationMapper.mapDTOReservationToDomain(dtoReservation);

        // Assert
        assertEquals(original.getId(), converted.getId());
        assertEquals(original.getBookId(), converted.getBookId());
        assertEquals(original.getUsername(), converted.getUsername());
        assertEquals(original.getReservationDate(), converted.getReservationDate());
    }

    @Test
    @DisplayName("Should handle different dates")
    void testMapDTOReservationToDomain_DifferentDates() {
        // Past date
        DTOReservation dto1 = DTOReservation.newBuilder()
            .setReservationDate("2020-01-01")
            .build();
        Reservation result1 = reservationMapper.mapDTOReservationToDomain(dto1);
        assertEquals(Date.valueOf("2020-01-01"), result1.getReservationDate());

        // Future date
        DTOReservation dto2 = DTOReservation.newBuilder()
            .setReservationDate("2030-12-31")
            .build();
        Reservation result2 = reservationMapper.mapDTOReservationToDomain(dto2);
        assertEquals(Date.valueOf("2030-12-31"), result2.getReservationDate());
    }

    @Test
    @DisplayName("Should handle different position in queue values")
    void testMapDomainToReservationDTO_DifferentPositions() {
        // First in queue
        Reservation res1 = new Reservation(1, "user1", 100, Date.valueOf("2025-01-01"), 1);
        ReservationDTO dto1 = reservationMapper.mapDomainToReservationDTO(res1);
        assertEquals(1, dto1.getPositionInQueue());

        // Later in queue
        Reservation res2 = new Reservation(2, "user2", 100, Date.valueOf("2025-01-01"), 10);
        ReservationDTO dto2 = reservationMapper.mapDomainToReservationDTO(res2);
        assertEquals(10, dto2.getPositionInQueue());
    }

    @Test
    @DisplayName("Should handle zero position in queue")
    void testMapDomainToReservationDTO_ZeroPosition() {
        // Arrange
        Reservation reservation = new Reservation(1, "user", 100, Date.valueOf("2025-01-01"), 0);

        // Act
        ReservationDTO result = reservationMapper.mapDomainToReservationDTO(reservation);

        // Assert
        assertEquals(0, result.getPositionInQueue());
    }

    @Test
    @DisplayName("Should handle large book IDs")
    void testMapDomainToDTOReservation_LargeBookId() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setId(1);
        reservation.setBookId(999999);
        reservation.setUsername("user");
        reservation.setReservationDate(Date.valueOf("2025-01-01"));

        // Act
        DTOReservation result = reservationMapper.mapDomainToDTOReservation(reservation);

        // Assert
        assertEquals(999999, result.getBookId());
    }

    @Test
    @DisplayName("Should handle large reservation IDs")
    void testMapDomainToReservationDTO_LargeId() {
        // Arrange
        Reservation reservation = new Reservation(999999, "user", 100, Date.valueOf("2025-01-01"), 1);

        // Act
        ReservationDTO result = reservationMapper.mapDomainToReservationDTO(reservation);

        // Assert
        assertEquals(999999, result.getId());
    }
}


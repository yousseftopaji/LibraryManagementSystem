package dk.via.sep3.controller;

import dk.via.sep3.application.domain.Reservation;
import dk.via.sep3.application.services.reservation.ReservationService;
import dk.via.sep3.mapper.ReservationMapper.ReservationMapper;
import dk.via.sep3.DTOs.reservation.CreateReservationDTO;
import dk.via.sep3.DTOs.reservation.ReservationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReservationController
 * Tests reservation creation functionality with various scenarios
 */
@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private ReservationController reservationController;

    private CreateReservationDTO createReservationDTO;
    private Reservation domainReservation;
    private Reservation createdReservation;
    private ReservationDTO reservationDTO;

    @BeforeEach
    void setUp() {
        // Setup CreateReservationDTO
        createReservationDTO = new CreateReservationDTO("testuser", "1234567890");

        // Setup domain reservation (before creation)
        domainReservation = new Reservation();
        domainReservation.setUsername("testuser");
        domainReservation.setBookId(1);
        domainReservation.setReservationDate(Date.valueOf(LocalDate.now()));

        // Setup created reservation (after creation)
        createdReservation = new Reservation();
        createdReservation.setId(1);
        createdReservation.setUsername("testuser");
        createdReservation.setBookId(1);
        createdReservation.setReservationDate(Date.valueOf(LocalDate.now()));

        // Setup ReservationDTO
        reservationDTO = new ReservationDTO(1, "testuser", 1, Date.valueOf(LocalDate.now()), 1);
    }

    @Test
    @DisplayName("Should successfully create reservation when valid request is provided")
    void testCreateReservation_Success() {
        // Arrange
        when(reservationMapper.mapCreateReservationDTOToDomain(createReservationDTO)).thenReturn(domainReservation);
        when(reservationService.createReservation(domainReservation)).thenReturn(createdReservation);
        when(reservationMapper.mapDomainToReservationDTO(createdReservation)).thenReturn(reservationDTO);

        // Act
        ResponseEntity<ReservationDTO> response = reservationController.createReservation(createReservationDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals(1, response.getBody().getBookId());
        assertNotNull(response.getBody().getReservationDate());

        // Verify interactions
        verify(reservationMapper, times(1)).mapCreateReservationDTOToDomain(createReservationDTO);
        verify(reservationService, times(1)).createReservation(domainReservation);
        verify(reservationMapper, times(1)).mapDomainToReservationDTO(createdReservation);
    }

    @Test
    @DisplayName("Should throw exception when creating reservation with invalid username")
    void testCreateReservation_InvalidUsername() {
        // Arrange
        CreateReservationDTO invalidDTO = new CreateReservationDTO("", "1234567890");

        Reservation invalidReservation = new Reservation();
        invalidReservation.setUsername("");

        when(reservationMapper.mapCreateReservationDTOToDomain(invalidDTO)).thenReturn(invalidReservation);
        when(reservationService.createReservation(invalidReservation))
                .thenThrow(new IllegalArgumentException("Username cannot be empty"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> reservationController.createReservation(invalidDTO));

        // Verify interactions
        verify(reservationMapper, times(1)).mapCreateReservationDTOToDomain(invalidDTO);
        verify(reservationService, times(1)).createReservation(invalidReservation);
        verify(reservationMapper, never()).mapDomainToReservationDTO(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw exception when creating reservation with null username")
    void testCreateReservation_NullUsername() {
        // Arrange
        CreateReservationDTO invalidDTO = new CreateReservationDTO(null, "1234567890");

        Reservation invalidReservation = new Reservation();
        invalidReservation.setUsername(null);

        when(reservationMapper.mapCreateReservationDTOToDomain(invalidDTO)).thenReturn(invalidReservation);
        when(reservationService.createReservation(invalidReservation))
                .thenThrow(new IllegalArgumentException("Username cannot be null"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> reservationController.createReservation(invalidDTO));

        // Verify interactions
        verify(reservationMapper, times(1)).mapCreateReservationDTOToDomain(invalidDTO);
        verify(reservationService, times(1)).createReservation(invalidReservation);
        verify(reservationMapper, never()).mapDomainToReservationDTO(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw exception when creating reservation with invalid book ISBN")
    void testCreateReservation_InvalidBookISBN() {
        // Arrange
        CreateReservationDTO invalidDTO = new CreateReservationDTO("testuser", "");

        Reservation invalidReservation = new Reservation();
        invalidReservation.setUsername("testuser");

        when(reservationMapper.mapCreateReservationDTOToDomain(invalidDTO)).thenReturn(invalidReservation);
        when(reservationService.createReservation(invalidReservation))
                .thenThrow(new IllegalArgumentException("Book ISBN cannot be empty"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> reservationController.createReservation(invalidDTO));

        // Verify interactions
        verify(reservationMapper, times(1)).mapCreateReservationDTOToDomain(invalidDTO);
        verify(reservationService, times(1)).createReservation(invalidReservation);
        verify(reservationMapper, never()).mapDomainToReservationDTO(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw exception when book not found")
    void testCreateReservation_BookNotFound() {
        // Arrange
        when(reservationMapper.mapCreateReservationDTOToDomain(createReservationDTO)).thenReturn(domainReservation);
        when(reservationService.createReservation(domainReservation))
                .thenThrow(new IllegalArgumentException("Book not found with ISBN: 1234567890"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> reservationController.createReservation(createReservationDTO));

        // Verify interactions
        verify(reservationMapper, times(1)).mapCreateReservationDTOToDomain(createReservationDTO);
        verify(reservationService, times(1)).createReservation(domainReservation);
        verify(reservationMapper, never()).mapDomainToReservationDTO(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testCreateReservation_UserNotFound() {
        // Arrange
        when(reservationMapper.mapCreateReservationDTOToDomain(createReservationDTO)).thenReturn(domainReservation);
        when(reservationService.createReservation(domainReservation))
                .thenThrow(new IllegalArgumentException("User not found with username: testuser"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> reservationController.createReservation(createReservationDTO));

        // Verify interactions
        verify(reservationMapper, times(1)).mapCreateReservationDTOToDomain(createReservationDTO);
        verify(reservationService, times(1)).createReservation(domainReservation);
        verify(reservationMapper, never()).mapDomainToReservationDTO(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw exception when book is already available")
    void testCreateReservation_BookAlreadyAvailable() {
        // Arrange
        when(reservationMapper.mapCreateReservationDTOToDomain(createReservationDTO)).thenReturn(domainReservation);
        when(reservationService.createReservation(domainReservation))
                .thenThrow(new IllegalStateException("Cannot reserve an available book"));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> reservationController.createReservation(createReservationDTO));

        // Verify interactions
        verify(reservationMapper, times(1)).mapCreateReservationDTOToDomain(createReservationDTO);
        verify(reservationService, times(1)).createReservation(domainReservation);
        verify(reservationMapper, never()).mapDomainToReservationDTO(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw exception when user already has reservation for the book")
    void testCreateReservation_DuplicateReservation() {
        // Arrange
        when(reservationMapper.mapCreateReservationDTOToDomain(createReservationDTO)).thenReturn(domainReservation);
        when(reservationService.createReservation(domainReservation))
                .thenThrow(new IllegalStateException("User already has a reservation for this book"));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> reservationController.createReservation(createReservationDTO));

        // Verify interactions
        verify(reservationMapper, times(1)).mapCreateReservationDTOToDomain(createReservationDTO);
        verify(reservationService, times(1)).createReservation(domainReservation);
        verify(reservationMapper, never()).mapDomainToReservationDTO(any(Reservation.class));
    }

    @Test
    @DisplayName("Should correctly map all reservation fields")
    void testCreateReservation_FieldMapping() {
        // Arrange
        when(reservationMapper.mapCreateReservationDTOToDomain(createReservationDTO)).thenReturn(domainReservation);
        when(reservationService.createReservation(domainReservation)).thenReturn(createdReservation);
        when(reservationMapper.mapDomainToReservationDTO(createdReservation)).thenReturn(reservationDTO);

        // Act
        ResponseEntity<ReservationDTO> response = reservationController.createReservation(createReservationDTO);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals(1, response.getBody().getBookId());
        assertNotNull(response.getBody().getReservationDate());

        // Verify interactions
        verify(reservationMapper, times(1)).mapCreateReservationDTOToDomain(createReservationDTO);
        verify(reservationService, times(1)).createReservation(domainReservation);
        verify(reservationMapper, times(1)).mapDomainToReservationDTO(createdReservation);
    }

    @Test
    @DisplayName("Should handle service layer exceptions gracefully")
    void testCreateReservation_ServiceException() {
        // Arrange
        when(reservationMapper.mapCreateReservationDTOToDomain(createReservationDTO)).thenReturn(domainReservation);
        when(reservationService.createReservation(domainReservation))
                .thenThrow(new RuntimeException("Unexpected database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> reservationController.createReservation(createReservationDTO));

        // Verify interactions
        verify(reservationMapper, times(1)).mapCreateReservationDTOToDomain(createReservationDTO);
        verify(reservationService, times(1)).createReservation(domainReservation);
        verify(reservationMapper, never()).mapDomainToReservationDTO(any(Reservation.class));
    }
}


package dk.via.sep3.controller;

import dk.via.sep3.application.domain.Reservation;
import dk.via.sep3.application.services.reservation.ReservationService;
import dk.via.sep3.mapper.ReservationMapper.ReservationMapper;
import dk.via.sep3.DTOs.reservation.CreateReservationDTO;
import dk.via.sep3.DTOs.reservation.ReservationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationControllerTest {

  private ReservationService reservationService;
  private ReservationMapper reservationMapper;
  private ReservationController controller;

  @BeforeEach
  void setUp() {
    reservationService = mock(ReservationService.class);
    reservationMapper = mock(ReservationMapper.class);
    controller = new ReservationController(reservationService, reservationMapper);
  }

  @Test
  void createReservation_returnsCreatedReservationDTO() {
    // Arrange
    CreateReservationDTO dto = new CreateReservationDTO();
    dto.setUsername("john");
    dto.setBookISBN("123");

    Reservation domainReservation = new Reservation();
    Reservation createdReservation = new Reservation();
    createdReservation.setId(10);

    ReservationDTO reservationDTO =
        new ReservationDTO(10, "john", 5, null, 1);

    when(reservationMapper.mapCreateReservationDTOToDomain(dto))
        .thenReturn(domainReservation);

    when(reservationService.createReservation(domainReservation))
        .thenReturn(createdReservation);

    when(reservationMapper.mapDomainToReservationDTO(createdReservation))
        .thenReturn(reservationDTO);

    // Act
    ResponseEntity<ReservationDTO> response = controller.createReservation(dto);

    // Assert
    assertEquals(201, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals(10, response.getBody().getId());
    assertEquals("john", response.getBody().getUsername());

    verify(reservationMapper).mapCreateReservationDTOToDomain(dto);
    verify(reservationService).createReservation(domainReservation);
    verify(reservationMapper).mapDomainToReservationDTO(createdReservation);
  }
}

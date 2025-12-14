package dk.via.sep3.controller;

import dk.via.sep3.model.domain.Reservation;
import dk.via.sep3.model.reservation.ReservationService;
import dk.via.sep3.shared.mapper.ReservationMapper.ReservationMapper;
import dk.via.sep3.shared.reservation.CreateReservationDTO;
import dk.via.sep3.shared.reservation.ReservationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.ResponseEntity;

import java.sql.Date;

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
    // ----- Input DTO -----
    CreateReservationDTO createDto = new CreateReservationDTO();
    createDto.setUsername("alice");
    createDto.setBookISBN("isbn-123");

    // ----- Domain reservation created by mapper -----
    Reservation domainReservation = new Reservation();
    domainReservation.setUsername("alice");
    domainReservation.setBookId(1);

    // ----- Persisted reservation returned by service -----
    Reservation persisted = new Reservation();
    persisted.setId(42);
    persisted.setUsername("alice");
    persisted.setBookId(1);
    persisted.setReservationDate(Date.valueOf("2025-01-10"));

    // ----- Real ReservationDTO -----
    ReservationDTO dto = new ReservationDTO(
        42,
        "alice",
        1,
        Date.valueOf("2025-01-10"),
        1
    );

    // ----- Mock behavior -----
    when(reservationMapper.mapCreateReservationDTOToDomain(createDto)).thenReturn(domainReservation);
    when(reservationService.createReservation(domainReservation)).thenReturn(persisted);
    when(reservationMapper.mapDomainToReservationDTO(persisted)).thenReturn(dto);

    // ----- ACT -----
    ResponseEntity<ReservationDTO> response = controller.createReservation(createDto);

    // ----- ASSERT -----
    assertEquals(201, response.getStatusCodeValue());
    assertNotNull(response.getBody());
    assertEquals(42, response.getBody().getId());
    assertEquals("alice", response.getBody().getUsername());
    assertEquals(1, response.getBody().getBookId());
    assertEquals(Date.valueOf("2025-01-10"), response.getBody().getReservationDate());

    // ----- VERIFY -----
    verify(reservationMapper).mapCreateReservationDTOToDomain(createDto);
    verify(reservationService).createReservation(domainReservation);
    verify(reservationMapper).mapDomainToReservationDTO(persisted);
  }
}

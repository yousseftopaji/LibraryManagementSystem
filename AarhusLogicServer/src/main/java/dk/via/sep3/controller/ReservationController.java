package dk.via.sep3.controller;

import dk.via.sep3.model.domain.Reservation;
import dk.via.sep3.model.reservation.ReservationService;
import dk.via.sep3.shared.mapper.ReservationMapper.ReservationMapper;
import dk.via.sep3.shared.reservation.CreateReservationDTO;
import dk.via.sep3.shared.reservation.ReservationDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/reservations") public class ReservationController
{
  private final ReservationService reservationService;
  private final ReservationMapper reservationMapper;

  public ReservationController(ReservationService reservationService,
      ReservationMapper reservationMapper)
  {
    this.reservationService = reservationService;
    this.reservationMapper = reservationMapper;
  }
    @PreAuthorize("hasRole('Reader')")
  @PostMapping public ResponseEntity<ReservationDTO> createReservation(
      @RequestBody CreateReservationDTO createReservationDTO)
  {
    Reservation reservation = reservationMapper.mapCreateReservationDTOToDomain(createReservationDTO);
    Reservation createdReservation = reservationService.createReservation(reservation);
    ReservationDTO reservationDTO = reservationMapper.mapDomainToReservationDTO(createdReservation);
    return new ResponseEntity<> (reservationDTO, HttpStatus.CREATED);
  }
}
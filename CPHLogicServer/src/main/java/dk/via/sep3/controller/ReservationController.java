package dk.via.sep3.controller;

import dk.via.sep3.model.reservation.ReservationService;
import dk.via.sep3.shared.reservation.CreateReservationDTO;
import dk.via.sep3.shared.reservation.ReservationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/reservations") public class ReservationController
{
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService)
    {
        this.reservationService = reservationService;
    }

    @PostMapping public ResponseEntity<ReservationDTO> createReservation(
            @RequestBody CreateReservationDTO createReservationDTO)
    {
        ReservationDTO reservationDTO = reservationService.createReservation(
                createReservationDTO);
        return ResponseEntity.status(201).body(reservationDTO);
    }
}
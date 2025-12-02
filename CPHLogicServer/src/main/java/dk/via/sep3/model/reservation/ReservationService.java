package dk.via.sep3.model.reservation;

import dk.via.sep3.shared.reservation.CreateReservationDTO;
import dk.via.sep3.shared.reservation.ReservationDTO;

public interface ReservationService
{
    ReservationDTO createReservation(CreateReservationDTO createReservationDTO);
}
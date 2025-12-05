package dk.via.sep3.grpcConnection.reservationGrpcService;

import dk.via.sep3.DTOReservation;
import dk.via.sep3.model.domain.Reservation;
import dk.via.sep3.model.reservation.ReservationService;

import java.util.List;

public interface ReservationGrpcService
{
    Reservation createReservation(Reservation reservation);
    List<Reservation> getReservationsByIsbn(String isbn);
    int getReservationCountByISBN(String isbn);
}

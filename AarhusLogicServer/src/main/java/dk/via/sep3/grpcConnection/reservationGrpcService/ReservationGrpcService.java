package dk.via.sep3.grpcConnection.reservationGrpcService;

import dk.via.sep3.DTOReservation;
import dk.via.sep3.model.domain.Reservation;

public interface ReservationGrpcService
{
    Reservation createReservation(Reservation reservation);
    int getReservationCountByISBN(String isbn);
}

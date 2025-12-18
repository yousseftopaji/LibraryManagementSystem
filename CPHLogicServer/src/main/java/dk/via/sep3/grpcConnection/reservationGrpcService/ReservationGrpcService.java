package dk.via.sep3.grpcConnection.reservationGrpcService;

import dk.via.sep3.application.domain.Reservation;

import java.util.List;

public interface ReservationGrpcService
{
    Reservation createReservation(Reservation reservation);
    int getReservationCountByISBN(String isbn);
    List<Reservation> getReservationsByIsbn(String isbn);
}

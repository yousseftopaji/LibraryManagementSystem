package dk.via.sep3.grpcConnection.reservationPersistenceService;

import dk.via.sep3.DTOReservation;

import java.util.List;

public interface ReservationPersistenceService {
    DTOReservation createReservation(String username, String bookId, String reservationDate);
    int getReservationCountByISBN(String isbn);
}

package dk.via.sep3.grpcConnection.reserveGrpcService;

import dk.via.sep3.DTOReserve;

import java.util.List;

public interface ReserveGrpcService {
    DTOReserve createReserve(String username, String bookId,int queueNumber, String now);
    List<DTOReserve> getReservationsByIsbn(String isbn);
}

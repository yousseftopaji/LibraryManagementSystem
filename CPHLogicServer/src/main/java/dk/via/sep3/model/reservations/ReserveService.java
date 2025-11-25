package dk.via.sep3.model.reservations;

import dk.via.sep3.shared.reserve.CreateReserveDTO;
import dk.via.sep3.shared.reserve.ReserveDTO;

public interface ReserveService {
    ReserveDTO createReserve(CreateReserveDTO createReserveDTO);
}

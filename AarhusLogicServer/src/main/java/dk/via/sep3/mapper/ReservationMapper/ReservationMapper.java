package dk.via.sep3.mapper.ReservationMapper;

import dk.via.sep3.DTOReservation;
import dk.via.sep3.model.domain.Reservation;
import dk.via.sep3.shared.reservation.CreateReservationDTO;
import dk.via.sep3.shared.reservation.ReservationDTO;

public interface ReservationMapper
{
  Reservation mapCreateReservationDTOToDomain(CreateReservationDTO createReservationDTO);

  DTOReservation mapDomainToDTOReservation(Reservation reservation);

  Reservation mapDTOReservationToDomain(DTOReservation dtoReservation);

  ReservationDTO mapDomainToReservationDTO(Reservation reservation);
}

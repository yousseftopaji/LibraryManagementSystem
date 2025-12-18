package dk.via.sep3.mapper.ReservationMapper;

import dk.via.sep3.DTOReservation;
import dk.via.sep3.application.domain.Reservation;
import dk.via.sep3.DTOs.reservation.CreateReservationDTO;
import dk.via.sep3.DTOs.reservation.ReservationDTO;

public interface ReservationMapper
{
  Reservation mapCreateReservationDTOToDomain(CreateReservationDTO createReservationDTO);

  DTOReservation mapDomainToDTOReservation(Reservation reservation);

  Reservation mapDTOReservationToDomain(DTOReservation dtoReservation);

  ReservationDTO mapDomainToReservationDTO(Reservation reservation);
}

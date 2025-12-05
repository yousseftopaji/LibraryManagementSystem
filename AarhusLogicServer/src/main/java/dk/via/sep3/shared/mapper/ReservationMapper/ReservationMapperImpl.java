package dk.via.sep3.shared.mapper.ReservationMapper;

import dk.via.sep3.DTOReservation;
import dk.via.sep3.model.domain.Reservation;
import dk.via.sep3.shared.reservation.CreateReservationDTO;
import dk.via.sep3.shared.reservation.ReservationDTO;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class ReservationMapperImpl implements ReservationMapper
{

  @Override public Reservation mapCreateReservationDTOToDomain(
      CreateReservationDTO createReservationDTO)
  {
    Reservation reservation = new Reservation();
    reservation.setUsername(createReservationDTO.getUsername());
    reservation.setBookISBN(createReservationDTO.getBookISBN());
    return reservation;
  }

  @Override public DTOReservation mapDomainToDTOReservation(
      Reservation reservation)
  {
    return DTOReservation.newBuilder()
        .setId(reservation.getId())
        .setBookId(reservation.getBookId())
        .setUsername(reservation.getUsername())
        .setReservationDate(reservation.getReservationDate().toString())
        .build();
  }

  @Override public Reservation mapDTOReservationToDomain(
      DTOReservation dtoReservation)
  {
    Date reservationDate = Date.valueOf(dtoReservation.getReservationDate());
    Reservation reservation = new Reservation();
    reservation.setId(dtoReservation.getId());
    reservation.setBookId(dtoReservation.getBookId());
    reservation.setUsername(dtoReservation.getUsername());
    reservation.setReservationDate(reservationDate);
    return reservation;
  }

  @Override public ReservationDTO mapDomainToReservationDTO(
      Reservation reservation)
  {
    return new ReservationDTO(
        reservation.getId(),
        reservation.getUsername(),
        reservation.getBookId(),
        reservation.getReservationDate(),
        reservation.getPositionInQueue()
    );
  }
}

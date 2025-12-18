package dk.via.sep3.mapper.ReservationMapper;

import dk.via.sep3.DTOReservation;
import dk.via.sep3.application.domain.Reservation;
import dk.via.sep3.DTOs.reservation.CreateReservationDTO;
import dk.via.sep3.DTOs.reservation.ReservationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class ReservationMapperImplTest {

  private ReservationMapperImpl mapper;

  @BeforeEach
  void setUp() {
    mapper = new ReservationMapperImpl();
  }

  // --------------------------------------------------
  // mapCreateReservationDTOToDomain
  // --------------------------------------------------

  @Test
  void mapCreateReservationDTOToDomain_mapsFieldsCorrectly() {
    CreateReservationDTO dto = new CreateReservationDTO("john", "ISBN-123");

    Reservation reservation = mapper.mapCreateReservationDTOToDomain(dto);

    assertEquals("john", reservation.getUsername());
    assertEquals("ISBN-123", reservation.getBookISBN());
  }

  // --------------------------------------------------
  // mapDomainToDTOReservation (Domain -> Proto)
  // --------------------------------------------------

  @Test
  void mapDomainToDTOReservation_mapsAllFieldsCorrectly() {
    Reservation reservation = new Reservation();
    reservation.setId(5);
    reservation.setBookId(99);
    reservation.setUsername("alice");
    reservation.setReservationDate(Date.valueOf("2025-01-10"));

    DTOReservation dto = mapper.mapDomainToDTOReservation(reservation);

    assertEquals(5, dto.getId());
    assertEquals(99, dto.getBookId());
    assertEquals("alice", dto.getUsername());
    assertEquals("2025-01-10", dto.getReservationDate());
  }

  // --------------------------------------------------
  // mapDTOReservationToDomain (Proto -> Domain)
  // --------------------------------------------------

  @Test
  void mapDTOReservationToDomain_mapsAllFieldsCorrectly() {
    DTOReservation dto = DTOReservation.newBuilder()
        .setId(7)
        .setBookId(55)
        .setUsername("bob")
        .setReservationDate("2025-02-15")
        .build();

    Reservation reservation = mapper.mapDTOReservationToDomain(dto);

    assertEquals(7, reservation.getId());
    assertEquals(55, reservation.getBookId());
    assertEquals("bob", reservation.getUsername());
    assertEquals(Date.valueOf("2025-02-15"), reservation.getReservationDate());
  }

  // --------------------------------------------------
  // mapDomainToReservationDTO (Domain -> REST DTO)
  // --------------------------------------------------

  @Test
  void mapDomainToReservationDTO_mapsAllFieldsCorrectly() {
    Reservation reservation = new Reservation();
    reservation.setId(10);
    reservation.setUsername("charlie");
    reservation.setBookId(77);
    reservation.setReservationDate(Date.valueOf("2025-03-01"));
    reservation.setPositionInQueue(3);

    ReservationDTO dto = mapper.mapDomainToReservationDTO(reservation);

    assertEquals(10, dto.getId());
    assertEquals("charlie", dto.getUsername());
    assertEquals(77, dto.getBookId());
    assertEquals(Date.valueOf("2025-03-01"), dto.getReservationDate());
    assertEquals(3, dto.getPositionInQueue());
  }
}

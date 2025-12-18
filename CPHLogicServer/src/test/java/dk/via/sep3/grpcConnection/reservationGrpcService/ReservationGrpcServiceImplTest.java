package dk.via.sep3.grpcConnection.reservationGrpcService;

import dk.via.sep3.*;
import dk.via.sep3.application.domain.Reservation;
import dk.via.sep3.exceptionHandler.GrpcCommunicationException;
import dk.via.sep3.mapper.ReservationMapper.ReservationMapper;
import io.grpc.ManagedChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationGrpcServiceImplTest {

  private ReservationServiceGrpc.ReservationServiceBlockingStub reservationStub;
  private ReservationMapper reservationMapper;
  private ReservationGrpcServiceImpl service;

  @BeforeEach
  void setUp() {
    reservationStub = mock(ReservationServiceGrpc.ReservationServiceBlockingStub.class);
    reservationMapper = mock(ReservationMapper.class);

    ManagedChannel channel = mock(ManagedChannel.class);
    service = new ReservationGrpcServiceImpl(channel, reservationMapper);

    injectStub(service, reservationStub);
  }

  private void injectStub(ReservationGrpcServiceImpl service,
      ReservationServiceGrpc.ReservationServiceBlockingStub stub) {
    try {
      var field = ReservationGrpcServiceImpl.class.getDeclaredField("reservationStub");
      field.setAccessible(true);
      field.set(service, stub);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // ---------------------------------------------------
  // createReservation()
  // ---------------------------------------------------

  @Test
  void createReservation_success_returnsMappedReservation() {
    Reservation reservation = new Reservation();
    reservation.setUsername("john");
    reservation.setBookId(1);
    reservation.setReservationDate(Date.valueOf("2024-01-01"));

    DTOReservation dtoReservation = DTOReservation.newBuilder()
        .setId(5)
        .build();

    CreateReservationResponse response = CreateReservationResponse.newBuilder()
        .setSuccess(true)
        .setReservation(dtoReservation)
        .build();

    when(reservationStub.createReservation(any(CreateReservationRequest.class)))
        .thenReturn(response);
    when(reservationMapper.mapDTOReservationToDomain(dtoReservation))
        .thenReturn(new Reservation());

    Reservation result = service.createReservation(reservation);

    assertNotNull(result);
    verify(reservationStub).createReservation(any(CreateReservationRequest.class));
    verify(reservationMapper).mapDTOReservationToDomain(dtoReservation);
  }

  @Test
  void createReservation_failure_returnsNull() {
    CreateReservationResponse response = CreateReservationResponse.newBuilder()
        .setSuccess(false)
        .setMessage("error")
        .build();

    when(reservationStub.createReservation(any(CreateReservationRequest.class)))
        .thenReturn(response);

    Reservation result = service.createReservation(new Reservation());

    assertNull(result);
  }

  @Test
  void createReservation_exception_throwsGrpcCommunicationException() {
    when(reservationStub.createReservation(any(CreateReservationRequest.class)))
        .thenThrow(RuntimeException.class);

    assertThrows(GrpcCommunicationException.class,
        () -> service.createReservation(new Reservation()));
  }

  // ---------------------------------------------------
  // getReservationsByIsbn()
  // ---------------------------------------------------

  @Test
  void getReservationsByIsbn_success_returnsMappedList() {
    DTOReservation dtoReservation = DTOReservation.newBuilder()
        .setId(1)
        .build();

    GetReservationsByIsbnResponse response =
        GetReservationsByIsbnResponse.newBuilder()
            .setSuccess(true)
            .addReservations(dtoReservation)
            .build();

    when(reservationStub.getReservationsByIsbn(any(GetReservationsByIsbnRequest.class)))
        .thenReturn(response);
    when(reservationMapper.mapDTOReservationToDomain(dtoReservation))
        .thenReturn(new Reservation());

    List<Reservation> result = service.getReservationsByIsbn("123");

    assertEquals(1, result.size());
  }

  @Test
  void getReservationsByIsbn_failure_throwsRuntimeException() {
    GetReservationsByIsbnResponse response =
        GetReservationsByIsbnResponse.newBuilder()
            .setSuccess(false)
            .setMessage("error")
            .build();

    when(reservationStub.getReservationsByIsbn(any(GetReservationsByIsbnRequest.class)))
        .thenReturn(response);

    assertThrows(RuntimeException.class,
        () -> service.getReservationsByIsbn("123"));
  }

  // ---------------------------------------------------
  // getReservationCountByISBN()
  // ---------------------------------------------------

  @Test
  void getReservationCountByISBN_success_returnsCount() {
    GetReservationCountByIsbnResponse response =
        GetReservationCountByIsbnResponse.newBuilder()
            .setSuccess(true)
            .setNumberOfReservations(3)
            .build();

    when(reservationStub.getReservationCountByIsbn(any(GetReservationCountByIsbnRequest.class)))
        .thenReturn(response);

    int count = service.getReservationCountByISBN("123");

    assertEquals(3, count);
  }

  @Test
  void getReservationCountByISBN_failure_throwsRuntimeException() {
    GetReservationCountByIsbnResponse response =
        GetReservationCountByIsbnResponse.newBuilder()
            .setSuccess(false)
            .setMessage("error")
            .build();

    when(reservationStub.getReservationCountByIsbn(any(GetReservationCountByIsbnRequest.class)))
        .thenReturn(response);

    assertThrows(RuntimeException.class,
        () -> service.getReservationCountByISBN("123"));
  }
}

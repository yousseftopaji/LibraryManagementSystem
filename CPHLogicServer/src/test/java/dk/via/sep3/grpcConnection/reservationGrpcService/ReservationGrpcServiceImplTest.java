package dk.via.sep3.grpcConnection.reservationGrpcService;

import dk.via.sep3.*;
import dk.via.sep3.controller.exceptionHandler.GrpcCommunicationException;
import dk.via.sep3.model.domain.Reservation;
import dk.via.sep3.shared.mapper.ReservationMapper.ReservationMapper;
import io.grpc.ManagedChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationGrpcServiceImplTest {

  private ReservationServiceGrpc.ReservationServiceBlockingStub stub;
  private ReservationMapper mapper;
  private ReservationGrpcServiceImpl service;

  @BeforeEach
  void setUp() throws Exception {
    stub = mock(ReservationServiceGrpc.ReservationServiceBlockingStub.class);
    mapper = mock(ReservationMapper.class);

    ManagedChannel channel = mock(ManagedChannel.class);
    service = new ReservationGrpcServiceImpl(channel, mapper);

    //  Inject mocked stub via reflection
    Field stubField = ReservationGrpcServiceImpl.class
        .getDeclaredField("reservationStub");
    stubField.setAccessible(true);
    stubField.set(service, stub);
  }


  // createReservation()


  @Test
  void createReservation_success_returnsMappedReservation() {
    Reservation input = new Reservation();
    input.setUsername("alice");
    input.setBookId(1);
    input.setReservationDate(Date.valueOf("2025-01-10"));

    DTOReservation dto = DTOReservation.newBuilder()
        .setId(10)
        .setUsername("alice")
        .setBookId(1)
        .setReservationDate("2025-01-10")
        .build();

    CreateReservationResponse response = CreateReservationResponse.newBuilder()
        .setReservation(dto)
        .setSuccess(true)
        .build();

    Reservation mapped = new Reservation();
    mapped.setId(10);

    when(stub.createReservation(any(CreateReservationRequest.class)))
        .thenReturn(response);
    when(mapper.mapDTOReservationToDomain(dto)).thenReturn(mapped);

    Reservation result = service.createReservation(input);

    assertNotNull(result);
    assertEquals(10, result.getId());
  }

  @Test
  void createReservation_failure_returnsNull() {
    CreateReservationResponse response = CreateReservationResponse.newBuilder()
        .setSuccess(false)
        .setMessage("error")
        .build();

    when(stub.createReservation(any()))
        .thenReturn(response);

    Reservation result = service.createReservation(new Reservation());

    assertNull(result);
  }

  @Test
  void createReservation_exception_throwsGrpcCommunicationException() {
    when(stub.createReservation(any()))
        .thenThrow(new RuntimeException("grpc error"));

    assertThrows(GrpcCommunicationException.class,
        () -> service.createReservation(new Reservation()));
  }


  // getReservationsByIsbn()


  @Test
  void getReservationsByIsbn_success_returnsMappedList() {
    DTOReservation dto = DTOReservation.newBuilder()
        .setId(1)
        .setUsername("bob")
        .setBookId(2)
        .build();

    GetReservationsByIsbnResponse response =
        GetReservationsByIsbnResponse.newBuilder()
            .addReservations(dto)
            .setSuccess(true)
            .build();

    Reservation mapped = new Reservation();
    mapped.setId(1);

    when(stub.getReservationsByIsbn(any(GetReservationsByIsbnRequest.class)))
        .thenReturn(response);
    when(mapper.mapDTOReservationToDomain(dto)).thenReturn(mapped);

    List<Reservation> reservations =
        service.getReservationsByIsbn("isbn-123");

    assertEquals(1, reservations.size());
    assertEquals(1, reservations.get(0).getId());
  }

  @Test
  void getReservationsByIsbn_failure_throwsRuntimeException() {
    GetReservationsByIsbnResponse response =
        GetReservationsByIsbnResponse.newBuilder()
            .setSuccess(false)
            .setMessage("fail")
            .build();

    when(stub.getReservationsByIsbn(any()))
        .thenReturn(response);

    assertThrows(RuntimeException.class,
        () -> service.getReservationsByIsbn("isbn"));
  }


  // getReservationCountByISBN()


  @Test
  void getReservationCountByISBN_success_returnsCount() {
    GetReservationCountByIsbnResponse response =
        GetReservationCountByIsbnResponse.newBuilder()
            .setNumberOfReservations(3)
            .setSuccess(true)
            .build();

    when(stub.getReservationCountByIsbn(any(GetReservationCountByIsbnRequest.class)))
        .thenReturn(response);

    int count = service.getReservationCountByISBN("isbn-123");

    assertEquals(3, count);
  }

  @Test
  void getReservationCountByISBN_failure_throwsRuntimeException() {
    GetReservationCountByIsbnResponse response =
        GetReservationCountByIsbnResponse.newBuilder()
            .setSuccess(false)
            .setMessage("error")
            .build();

    when(stub.getReservationCountByIsbn(any()))
        .thenReturn(response);

    assertThrows(RuntimeException.class,
        () -> service.getReservationCountByISBN("isbn"));
  }
}

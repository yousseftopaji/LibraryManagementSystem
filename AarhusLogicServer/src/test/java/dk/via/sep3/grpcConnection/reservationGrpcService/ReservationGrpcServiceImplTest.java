package dk.via.sep3.grpcConnection.reservationGrpcService;

import dk.via.sep3.*;
import dk.via.sep3.exceptionHandler.GrpcCommunicationException;
import dk.via.sep3.mapper.ReservationMapper.ReservationMapper;
import dk.via.sep3.model.domain.Reservation;
import io.grpc.ManagedChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationGrpcServiceImplTest {

    @Mock
    private ManagedChannel channel;

    @Mock
    private ReservationServiceGrpc.ReservationServiceBlockingStub reservationStub;

    @Mock
    private ReservationMapper reservationMapper;

    private ReservationGrpcServiceImpl reservationGrpcService;

    @BeforeEach
    void setUp() {
        when(ReservationServiceGrpc.newBlockingStub(channel)).thenReturn(reservationStub);
        reservationGrpcService = new ReservationGrpcServiceImpl(channel, reservationMapper);
    }

    @Test
    @DisplayName("Should create reservation successfully")
    void testCreateReservation_Success() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setUsername("testuser");
        reservation.setBookId(1);
        reservation.setReservationDate(Date.valueOf("2025-01-01"));

        DTOReservation dtoReservation = DTOReservation.newBuilder()
            .setId(1)
            .setUsername("testuser")
            .setBookId(1)
            .setReservationDate("2025-01-01")
            .build();

        CreateReservationResponse response = CreateReservationResponse.newBuilder()
            .setSuccess(true)
            .setReservation(dtoReservation)
            .build();

        Reservation createdReservation = new Reservation();
        createdReservation.setId(1);
        createdReservation.setUsername("testuser");

        when(reservationStub.createReservation(any(CreateReservationRequest.class))).thenReturn(response);
        when(reservationMapper.mapDTOReservationToDomain(dtoReservation)).thenReturn(createdReservation);

        // Act
        Reservation result = reservationGrpcService.createReservation(reservation);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("testuser", result.getUsername());
        verify(reservationStub).createReservation(any(CreateReservationRequest.class));
    }

    @Test
    @DisplayName("Should return null when create reservation fails")
    void testCreateReservation_Failure() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setUsername("testuser");
        reservation.setBookId(1);
        reservation.setReservationDate(Date.valueOf("2025-01-01"));

        CreateReservationResponse response = CreateReservationResponse.newBuilder()
            .setSuccess(false)
            .setMessage("Failed to create reservation")
            .build();

        when(reservationStub.createReservation(any(CreateReservationRequest.class))).thenReturn(response);

        // Act
        Reservation result = reservationGrpcService.createReservation(reservation);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Should throw GrpcCommunicationException when create reservation encounters error")
    void testCreateReservation_Exception() {
        // Arrange
        Reservation reservation = new Reservation();
        reservation.setUsername("testuser");
        reservation.setBookId(1);
        reservation.setReservationDate(Date.valueOf("2025-01-01"));

        when(reservationStub.createReservation(any(CreateReservationRequest.class)))
            .thenThrow(new RuntimeException("gRPC error"));

        // Act & Assert
        assertThrows(GrpcCommunicationException.class, () -> reservationGrpcService.createReservation(reservation));
    }

    @Test
    @DisplayName("Should get reservations by ISBN successfully")
    void testGetReservationsByIsbn_Success() {
        // Arrange
        String isbn = "123456";
        DTOReservation dtoRes1 = DTOReservation.newBuilder()
            .setId(1)
            .setUsername("user1")
            .build();
        DTOReservation dtoRes2 = DTOReservation.newBuilder()
            .setId(2)
            .setUsername("user2")
            .build();

        GetReservationsByIsbnResponse response = GetReservationsByIsbnResponse.newBuilder()
            .setSuccess(true)
            .addReservations(dtoRes1)
            .addReservations(dtoRes2)
            .build();

        Reservation res1 = new Reservation();
        res1.setId(1);
        Reservation res2 = new Reservation();
        res2.setId(2);

        when(reservationStub.getReservationsByIsbn(any(GetReservationsByIsbnRequest.class))).thenReturn(response);
        when(reservationMapper.mapDTOReservationToDomain(dtoRes1)).thenReturn(res1);
        when(reservationMapper.mapDTOReservationToDomain(dtoRes2)).thenReturn(res2);

        // Act
        List<Reservation> result = reservationGrpcService.getReservationsByIsbn(isbn);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(reservationStub).getReservationsByIsbn(any(GetReservationsByIsbnRequest.class));
    }

    @Test
    @DisplayName("Should throw exception when get reservations by ISBN fails")
    void testGetReservationsByIsbn_Failure() {
        // Arrange
        String isbn = "123456";
        GetReservationsByIsbnResponse response = GetReservationsByIsbnResponse.newBuilder()
            .setSuccess(false)
            .setMessage("Failed to fetch reservations")
            .build();

        when(reservationStub.getReservationsByIsbn(any(GetReservationsByIsbnRequest.class))).thenReturn(response);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> reservationGrpcService.getReservationsByIsbn(isbn));
    }

    @Test
    @DisplayName("Should throw exception when get reservations by ISBN encounters error")
    void testGetReservationsByIsbn_Exception() {
        // Arrange
        String isbn = "123456";
        when(reservationStub.getReservationsByIsbn(any(GetReservationsByIsbnRequest.class)))
            .thenThrow(new RuntimeException("gRPC error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> reservationGrpcService.getReservationsByIsbn(isbn));
    }

    @Test
    @DisplayName("Should get reservation count by ISBN successfully")
    void testGetReservationCountByISBN_Success() {
        // Arrange
        String isbn = "123456";
        GetReservationCountByIsbnResponse response = GetReservationCountByIsbnResponse.newBuilder()
            .setSuccess(true)
            .setNumberOfReservations(5)
            .build();

        when(reservationStub.getReservationCountByIsbn(any(GetReservationCountByIsbnRequest.class))).thenReturn(response);

        // Act
        int result = reservationGrpcService.getReservationCountByISBN(isbn);

        // Assert
        assertEquals(5, result);
        verify(reservationStub).getReservationCountByIsbn(any(GetReservationCountByIsbnRequest.class));
    }

    @Test
    @DisplayName("Should throw exception when get reservation count fails")
    void testGetReservationCountByISBN_Failure() {
        // Arrange
        String isbn = "123456";
        GetReservationCountByIsbnResponse response = GetReservationCountByIsbnResponse.newBuilder()
            .setSuccess(false)
            .setMessage("Failed to get count")
            .build();

        when(reservationStub.getReservationCountByIsbn(any(GetReservationCountByIsbnRequest.class))).thenReturn(response);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> reservationGrpcService.getReservationCountByISBN(isbn));
    }

    @Test
    @DisplayName("Should throw exception when get reservation count encounters error")
    void testGetReservationCountByISBN_Exception() {
        // Arrange
        String isbn = "123456";
        when(reservationStub.getReservationCountByIsbn(any(GetReservationCountByIsbnRequest.class)))
            .thenThrow(new RuntimeException("gRPC error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> reservationGrpcService.getReservationCountByISBN(isbn));
    }

    @Test
    @DisplayName("Should return empty list when no reservations found")
    void testGetReservationsByIsbn_EmptyList() {
        // Arrange
        String isbn = "123456";
        GetReservationsByIsbnResponse response = GetReservationsByIsbnResponse.newBuilder()
            .setSuccess(true)
            .build();

        when(reservationStub.getReservationsByIsbn(any(GetReservationsByIsbnRequest.class))).thenReturn(response);

        // Act
        List<Reservation> result = reservationGrpcService.getReservationsByIsbn(isbn);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return zero when no reservations exist")
    void testGetReservationCountByISBN_Zero() {
        // Arrange
        String isbn = "123456";
        GetReservationCountByIsbnResponse response = GetReservationCountByIsbnResponse.newBuilder()
            .setSuccess(true)
            .setNumberOfReservations(0)
            .build();

        when(reservationStub.getReservationCountByIsbn(any(GetReservationCountByIsbnRequest.class))).thenReturn(response);

        // Act
        int result = reservationGrpcService.getReservationCountByISBN(isbn);

        // Assert
        assertEquals(0, result);
    }
}


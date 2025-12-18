package dk.via.sep3.grpcConnection.reservationGrpcService;

import dk.via.sep3.*;
import dk.via.sep3.exceptionHandler.BusinessRuleViolationException;
import dk.via.sep3.exceptionHandler.GrpcCommunicationException;
import dk.via.sep3.model.domain.Reservation;
import dk.via.sep3.mapper.ReservationMapper.ReservationMapper;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service public class ReservationGrpcServiceImpl
    implements ReservationGrpcService
{
  private static final Logger logger = LoggerFactory.getLogger(
      ReservationGrpcServiceImpl.class);
  private final ReservationServiceGrpc.ReservationServiceBlockingStub reservationStub;
  private final ReservationMapper reservationMapper;

  public ReservationGrpcServiceImpl(ManagedChannel channel,
      ReservationMapper reservationMapper)
  {
    this.reservationStub = ReservationServiceGrpc.newBlockingStub(channel);
    this.reservationMapper = reservationMapper;
  }

  @Override public Reservation createReservation(Reservation reservation)
  {
    try
    {
      CreateReservationRequest request = CreateReservationRequest.newBuilder()
          .setUsername(reservation.getUsername()).setBookId(reservation.getBookId())
          .setReservationDate(reservation.getReservationDate().toString()).build();

      logger.info(
          "Sending gRPC request to create reservation for user: {}, bookId: {}, date: {}",
          reservation.getUsername(), reservation.getBookId(), reservation.getReservationDate());

      CreateReservationResponse response = reservationStub.createReservation(
          request);
      return handleResponse(response);
    }
    catch (BusinessRuleViolationException ex)
    {
      logger.error("Invalid bookId format: {}", reservation.getBookId(), ex);
      throw new RuntimeException(
          "Reservation creation failed: " + ex.getMessage(), ex);
    }
    catch (Exception ex)
    {
      logger.error("Error creating reservation", ex);
      throw new GrpcCommunicationException(
          "Reservation creation failed: " + ex.getMessage(), ex);
    }
  }

  @Override public List<Reservation> getReservationsByIsbn(String isbn)
  {
    try
    {
      GetReservationsByIsbnRequest request = GetReservationsByIsbnRequest.newBuilder()
          .setIsbn(isbn).build();
      GetReservationsByIsbnResponse response = reservationStub.getReservationsByIsbn(
          request);
      if (response.getSuccess())
      {
        logger.info("Fetched {} reservations for ISBN {}",
            response.getReservationsList().size(), isbn);
        List<Reservation> reservations = new ArrayList<>();
        for (DTOReservation res : response.getReservationsList())
        {
          Reservation reservation = reservationMapper.mapDTOReservationToDomain(res);
          reservations.add(reservation);
        }
        return reservations;
      }
      else
      {
        logger.error("Failed to fetch reservations for ISBN {}: {}", isbn,
            response.getMessage());
        throw new RuntimeException(
            "Failed to fetch reservations: " + response.getMessage());
      }
    }
    catch (Exception ex)
    {
      logger.error("Error fetching reservations for ISBN: {}", isbn, ex);
      throw new RuntimeException(
          "Failed to fetch reservations: " + ex.getMessage(), ex);
    }
  }

  @Override public int getReservationCountByISBN(String isbn)
  {
    try
    {
      GetReservationCountByIsbnRequest request = GetReservationCountByIsbnRequest.newBuilder()
          .setIsbn(isbn).build();
      GetReservationCountByIsbnResponse response = reservationStub.getReservationCountByIsbn(
          request);
      if (response.getSuccess())
      {
        logger.info("Fetched reservation count for ISBN {}: {}", isbn,
            response.getNumberOfReservations());
        return response.getNumberOfReservations();
      }
      else
      {
        logger.error("Failed to fetch reservation count for ISBN {}: {}", isbn,
            response.getMessage());
        throw new RuntimeException(
            "Failed to fetch reservation count: " + response.getMessage());
      }
    }
    catch (Exception ex)
    {
      logger.error("Error fetching reservation count for ISBN: {}", isbn, ex);
      throw new RuntimeException(
          "Failed to fetch reservation count: " + ex.getMessage(), ex);
    }
  }

  private Reservation handleResponse(CreateReservationResponse response)
  {
    if (response.getSuccess())
    {
      logger.info("Reservation created successfully: {}",
          response.getReservation());
      return reservationMapper.mapDTOReservationToDomain(response.getReservation());
    }
    else
    {
      logger.error("Failed to create reservation: {}", response.getMessage());
      return null;
    }
  }
}

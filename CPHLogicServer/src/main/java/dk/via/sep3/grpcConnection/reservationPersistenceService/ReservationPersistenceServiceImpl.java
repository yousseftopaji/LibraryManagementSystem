package dk.via.sep3.grpcConnection.reservationPersistenceService;

import dk.via.sep3.*;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service public class ReservationPersistenceServiceImpl
        implements ReservationPersistenceService
{
    private static final Logger logger = LoggerFactory.getLogger(
            ReservationPersistenceServiceImpl.class);
    private final ReservationServiceGrpc.ReservationServiceBlockingStub reservationStub;

    public ReservationPersistenceServiceImpl(ManagedChannel channel)
    {
        this.reservationStub = ReservationServiceGrpc.newBlockingStub(channel);
    }

    @Override public DTOReservation createReservation(String username,
                                                      String bookId, String reservationDate)
    {
        try
        {
            int bookIdInt = parseBookId(bookId);
            CreateReservationRequest request = CreateReservationRequest.newBuilder().setUsername(username)
                    .setBookId(bookIdInt).setReservationDate(reservationDate).build();

            logger.info(
                    "Sending gRPC request to create reservation for user: {}, bookId: {}, date: {}",
                    username, bookId, reservationDate);

            CreateReservationResponse response = reservationStub.createReservation(request);
            return handleResponse(response);
        }
        catch (NumberFormatException ex)
        {
            logger.error("Invalid bookId format: {}", bookId, ex);
            throw new RuntimeException("Reservation creation failed: " + ex.getMessage(), ex);
        }
        catch (Exception ex)
        {
            logger.error("Error creating reservation", ex);
            throw new RuntimeException("Reservation creation failed: " + ex.getMessage(), ex);
        }
    }

    @Override public int getReservationCountByISBN(String isbn)
    {
        try
        {
            GetReservationCountByIsbnRequest request = GetReservationCountByIsbnRequest.newBuilder()
                    .setIsbn(isbn).build();
            GetReservationCountByIsbnResponse response = reservationStub.getReservationCountByIsbn(request);
            if (response.getSuccess())
            {
                logger.info("Fetched reservation count for ISBN {}: {}",
                        isbn, response.getNumberOfReservations());
                return response.getNumberOfReservations();
            }
            else
            {
                logger.error("Failed to fetch reservation count for ISBN {}: {}",
                        isbn, response.getMessage());
                throw new RuntimeException("Failed to fetch reservation count: " + response.getMessage());
            }
        }
        catch (Exception ex)
        {
            logger.error("Error fetching reservation count for ISBN: {}", isbn, ex);
            throw new RuntimeException("Failed to fetch reservation count: " + ex.getMessage(), ex);
        }
    }

    private int parseBookId(String bookId)
    {
        return Integer.parseInt(bookId);
    }

    private DTOReservation handleResponse(CreateReservationResponse response)
    {
        if (response.getSuccess())
        {
            logger.info("Reservation created successfully: {}",
                    response.getReservation());
            return response.getReservation();
        }
        else
        {
            logger.error("Failed to create reservation: {}", response.getMessage());
            return null;
        }
    }
}
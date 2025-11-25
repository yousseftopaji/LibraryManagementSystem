package dk.via.sep3.grpcConnection.reserveGrpcService;

import dk.via.sep3.*;
import dk.via.sep3.grpcConnection.loanGrpcService.LoanGrpcService;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReserveGrpcServiceImpl implements ReserveGrpcService {
    private static final Logger logger = LoggerFactory.getLogger(LoanGrpcService.class);
    private final ReserveServiceGrpc.ReserveServiceBlockingStub reserveStub;

    public ReserveGrpcServiceImpl(ManagedChannel channel) {
        this.reserveStub = ReserveServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public DTOReserve createReserve(String username, String bookId, int queueNumber, String now) {
        try {
            int bookIdInt = Integer.parseInt(bookId);
            CreateReserveRequest request = CreateReserveRequest.newBuilder()
                    .setUsername(username).setBookId(bookIdInt).setReserveDate(now).setQueueNumber(queueNumber).build();

            logger.info(
                    "Sending gRPC request to create reserve for user: {}, bookId: {}, queueNumber:{}, date: {}",
                    username, bookId, queueNumber, now);

            CreateReserveResponse response = reserveStub.createReserve(request);

            if (response.getSuccess()) {
                logger.info("Reserve created successfully: {}", response.getReserve());
                return response.getReserve();
            } else {
                logger.error("Failed to create reserve: {}", response.getMessage());
                return null;
            }
        } catch (NumberFormatException ex) {
            logger.error("Invalid bookId format: {}", bookId, ex);
            return null;
        } catch (Exception ex) {
            logger.error("Error creating loan", ex);
            return null;
        }
    }

    @Override
    public List<DTOReserve> getReservationsByIsbn(String isbn) {
        GetReservationsByIsbnRequest request = GetReservationsByIsbnRequest.newBuilder()
                .setIsbn(isbn)
                .build();

        GetReservationsByIsbnResponse response = reserveStub.getReservationsByIsbn(request);

        if (response.getSuccess()) {
            return response.getReservationsList();
        } else {
            return List.of();
        }
    }
}

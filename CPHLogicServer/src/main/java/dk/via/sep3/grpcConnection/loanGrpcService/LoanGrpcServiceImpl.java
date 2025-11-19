package dk.via.sep3.grpcConnection.loanGrpcService;

import dk.via.sep3.*;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanGrpcServiceImpl implements LoanGrpcService {
  private static final Logger logger = LoggerFactory.getLogger(LoanGrpcService.class);
  private final LoanServiceGrpc.LoanServiceBlockingStub loanStub;

  public LoanGrpcServiceImpl(ManagedChannel channel) {
    this.loanStub = LoanServiceGrpc.newBlockingStub(channel);
  }

  @Override public DTOLoan createLoan(String username, String bookId, String now, String dueDate)
  {
    try
    {
      int bookIdInt = Integer.parseInt(bookId);
      CreateLoanRequest request = CreateLoanRequest.newBuilder()
          .setUsername(username)
          .setBookId(bookIdInt)
          .setBorrowDate(now)
          .setDueDate(dueDate)
          .build();
      logger.info(
          "Sending gRPC request to create loan for user: {}, bookId: {}, dates: {} to {}",
          username, bookId, now, dueDate);
      CreateLoanResponse response = loanStub.createLoan(request);
      if (response.getSuccess())
      {
        logger.info("Loan created successfully: {}", response.getLoan());
        return response.getLoan();
      }
      else
      {
        logger.error("Failed to create loan: {}", response.getMessage());
        return null;
      }
    }
    catch (NumberFormatException ex)
    {
      logger.error("Invalid bookId format: {}", bookId, ex);
      return null;
    }
    catch (Exception ex)
    {
      logger.error("Error creating loan", ex);
      return null;
    }
  }
}

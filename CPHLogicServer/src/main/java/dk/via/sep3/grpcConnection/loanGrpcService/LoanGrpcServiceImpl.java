package dk.via.sep3.grpcConnection.loanGrpcService;

import dk.via.sep3.*;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service public class LoanGrpcServiceImpl implements LoanGrpcService
{
  private static final Logger logger = LoggerFactory.getLogger(
      LoanGrpcService.class);
  private final LoanServiceGrpc.LoanServiceBlockingStub loanStub;

  public LoanGrpcServiceImpl(ManagedChannel channel)
  {
    this.loanStub = LoanServiceGrpc.newBlockingStub(channel);
  }

  @Override public DTOLoan createLoan(String username, String bookId,
      String now, String dueDate)
  {
    try
    {
      int bookIdInt = Integer.parseInt(bookId);
      CreateLoanRequest request = CreateLoanRequest.newBuilder()
          .setUsername(username).setBookId(bookIdInt).setBorrowDate(now)
          .setDueDate(dueDate).build();
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

  @Override public List<DTOLoan> getLoansByISBN(String isbn)
  {
    GetLoansByISBNRequest request = GetLoansByISBNRequest.newBuilder()
        .setIsbn(isbn).build();

    logger.info("Sending gRPC request to get loans for ISBN: {}", isbn);
    GetLoansByISBNResponse response = loanStub.getLoansByISBN(request);

    logger.info("Received {} loans for ISBN: {}", response.getLoansCount(),
        isbn);
    return response.getLoansList();
  }

  @Override public void extendLoan(int loanId, String username)
  {
    try
    {
      ExtendLoanRequest request = ExtendLoanRequest.newBuilder()
          .setLoanId(loanId).build();
      logger.info("Sending gRPC request to extend loan with ID: {}", loanId);
      ExtendLoanResponse response = loanStub.extendLoan(request);
      if (response.getSuccess())
      {
        logger.info("Loan with ID: {} extended successfully", loanId);
      }
      else
      {
        logger.error("Failed to extend loan with ID: {}: {}", loanId,
            response.getMessage());
        throw new RuntimeException("Failed to extend loan with ID: " + loanId);
      }
    }
    catch (Exception ex)
    {
      logger.error("Error extending loan with ID: {}", loanId, ex);
      throw new RuntimeException("Error extending loan with ID: " + loanId, ex);
    }
  }

  @Override public DTOLoan getLoanById(int bookId)
  {
    try
    {
      GetLoanByIdRequest request = GetLoanByIdRequest.newBuilder().setId(bookId)
          .build();

      logger.info("Sending gRPC request to get loan with ID: {}", bookId);
      GetLoanByIdResponse response = loanStub.getLoanById(request);
      if (response.getSuccess())
      {
        logger.info("Retrieved loan with ID: {}", bookId);
        return response.getLoan();
      }
      else
      {
        logger.error("Failed to retrieve loan with ID: {}: {}", bookId,
            response.getMessage());
        return null;
      }
    }
    catch (Exception ex)
    {
      logger.error("Error retrieving loan with ID: {}", bookId, ex);
      return null;
    }
  }
}

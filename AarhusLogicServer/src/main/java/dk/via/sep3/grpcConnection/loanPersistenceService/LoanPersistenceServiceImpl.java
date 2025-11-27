package dk.via.sep3.grpcConnection.loanPersistenceService;

import dk.via.sep3.*;
import dk.via.sep3.CreateLoanRequest;
import dk.via.sep3.CreateLoanResponse;
import dk.via.sep3.DTOLoan;
import dk.via.sep3.GetLoansByISBNRequest;
import dk.via.sep3.GetLoansByISBNResponse;
import dk.via.sep3.LoanServiceGrpc;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service public class LoanPersistenceServiceImpl
    implements LoanPersistenceService
{
  private static final Logger logger = LoggerFactory.getLogger(
      LoanPersistenceServiceImpl.class);
  private final LoanServiceGrpc.LoanServiceBlockingStub loanStub;

  public LoanPersistenceServiceImpl(ManagedChannel channel)
  {
    this.loanStub = LoanServiceGrpc.newBlockingStub(channel);
  }

  // java
  @Override public DTOLoan createLoan(String username, int bookId,
      String now, String dueDate)
  {
    try
    {
      CreateLoanRequest request = CreateLoanRequest.newBuilder().setUsername(username)
          .setBookId(bookId).setBorrowDate(now).setDueDate(dueDate).build();

      logger.info(
          "Sending gRPC request to create loan for user: {}, bookId: {}, dates: {} to {}",
          username, bookId, now, dueDate);
      CreateLoanResponse response = loanStub.createLoan(request);
      return handleResponse(response);
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
    GetLoansByISBNRequest request = GetLoansByISBNRequest
        .newBuilder()
        .setIsbn(isbn)
        .build();

    logger.info("Sending gRPC request to get loans for ISBN: {}", isbn);
    GetLoansByISBNResponse response = loanStub.getLoansByISBN(request);

    logger.info("Received {} loans for ISBN: {}", response.getLoansCount(), isbn);
    return response.getLoansList();
  }

  @Override public void extendLoan(int LoanId)
  {
    try
    {
      ExtendLoanRequest request = ExtendLoanRequest.newBuilder().setLoanId(LoanId).build();

      logger.info("Sending gRPC request to extend loan with ID: {}", LoanId);
      ExtendLoanResponse response = loanStub.extendLoan(request);
      if (response.getSuccess())
      {
        logger.info("Loan with ID: {} extended successfully", LoanId);
      }
      else
      {
        logger.error("Failed to extend loan with ID: {}: {}", LoanId, response.getMessage());
        throw new RuntimeException("Failed to extend loan with ID: " + LoanId);
      }
    }
    catch (Exception ex)
    {
      logger.error("Error extending loan with ID: {}", LoanId, ex);
      throw new RuntimeException("Error extending loan with ID: " + LoanId, ex);
    }
  }

  @Override
  public DTOLoan getLoanById(int loanId)
  {
    try
    {
      GetLoanByIdRequest request = GetLoanByIdRequest.newBuilder().setId(loanId).build();

      logger.info("Sending gRPC request to get loan with ID: {}", loanId);
      GetLoanByIdResponse response = loanStub.getLoanById(request);
      if (response.getSuccess())
      {
        logger.info("Retrieved loan with ID: {}", loanId);
        return response.getLoan();
      }
      else
      {
        logger.error("Failed to retrieve loan with ID: {}: {}", loanId, response.getMessage());
        return null;
      }
    }
    catch (Exception ex)
    {
      logger.error("Error retrieving loan with ID: {}", loanId, ex);
      return null;
    }
  }

  private DTOLoan handleResponse(CreateLoanResponse response)
  {
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
}

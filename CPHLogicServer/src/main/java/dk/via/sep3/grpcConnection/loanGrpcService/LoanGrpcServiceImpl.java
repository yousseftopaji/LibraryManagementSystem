package dk.via.sep3.grpcConnection.loanGrpcService;

import dk.via.sep3.*;
import dk.via.sep3.model.domain.Loan;
import dk.via.sep3.shared.mapper.loanMapper.LoanMapper;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service public class LoanGrpcServiceImpl implements LoanGrpcService
{
  private static final Logger logger = LoggerFactory.getLogger(
      LoanGrpcServiceImpl.class);
  private final LoanServiceGrpc.LoanServiceBlockingStub loanStub;
  private final LoanMapper loanMapper;
  public LoanGrpcServiceImpl(ManagedChannel channel, LoanMapper loanMapper)
  {
    this.loanStub = LoanServiceGrpc.newBlockingStub(channel);
    this.loanMapper = loanMapper;
  }

  @Override public Loan createLoan(Loan loan)
  {
    try
    {
      CreateLoanRequest request = CreateLoanRequest.newBuilder()
          .setUsername(loan.getUsername()).setBookId(loan.getBookId()).setBorrowDate(loan.getBorrowDate().toString())
          .setDueDate(loan.getDueDate().toString()).build();
      logger.info(
          "Sending gRPC request to create loan for user: {}, bookId: {}, dates: {} to {}",
          loan.getUsername(), loan.getBookId(), loan.getBorrowDate(), loan.getDueDate());
      CreateLoanResponse response = loanStub.createLoan(request);
      if (response.getSuccess())
      {
        logger.info("Loan created successfully: {}", response.getLoan());
        return loanMapper.mapDTOLoanToDomain(response.getLoan());
      }
      else
      {
        logger.error("Failed to create loan: {}", response.getMessage());
        return null;
      }
    }
    catch (Exception ex)
    {
      logger.error("Error creating loan", ex);
      return null;
    }
  }

  @Override public List<Loan> getLoansByISBN(String isbn)
  {
    GetLoansByISBNRequest request = GetLoansByISBNRequest.newBuilder()
        .setIsbn(isbn).build();

    logger.info("Sending gRPC request to get loans for ISBN: {}", isbn);
    GetLoansByISBNResponse response = loanStub.getLoansByISBN(request);

    logger.info("Received {} loans for ISBN: {}", response.getLoansCount(),
        isbn);
    List<Loan> loans = new ArrayList<>();
    for (DTOLoan l : response.getLoansList())
    {
      loans.add(loanMapper.mapDTOLoanToDomain(l));
    }
    return loans;
  }

  @Override public void extendLoan(Loan loan)
  {
    try
    {
      DTOLoan dtoLoan = loanMapper.mapDomainToDTOLoan(loan);
      ExtendLoanRequest request = ExtendLoanRequest.newBuilder().setLoan(dtoLoan).build();
      logger.info("Sending gRPC request to extend loan with ID: {}", loan.getLoanId());
      ExtendLoanResponse response = loanStub.extendLoan(request);
      if (response.getSuccess())
      {
        logger.info("Loan with ID: {} extended successfully", loan.getLoanId());
      }
      else
      {
        logger.error("Failed to extend loan with ID: {}: {}", loan.getLoanId(),
            response.getMessage());
        throw new RuntimeException("Failed to extend loan with ID: " + loan.getLoanId());
      }
    }
    catch (Exception ex)
    {
      logger.error("Error extending loan with ID: {}", loan.getLoanId(), ex);
      throw new RuntimeException("Error extending loan with ID: " + loan.getLoanId(), ex);
    }
  }

  @Override public Loan getLoanById(int bookId)
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
        return loanMapper.mapDTOLoanToDomain(response.getLoan());
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
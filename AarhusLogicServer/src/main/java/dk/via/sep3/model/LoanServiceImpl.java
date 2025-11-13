package dk.via.sep3.model;

import dk.via.sep3.DTOLoan;
import dk.via.sep3.grpcConnection.GrpcConnectionInterface;

public class LoanServiceImpl implements LoanService
{
  private final GrpcConnectionInterface grpcConnectionInterface;

  public LoanServiceImpl(GrpcConnectionInterface grpcConnectionInterface)
  {
    this.grpcConnectionInterface = grpcConnectionInterface;
  }

  @Override
  public DTOLoan createLoan(String username, String bookId, int loanDurationDays)
  {
    return grpcConnectionInterface.createLoan(username, bookId, loanDurationDays);
  }
}


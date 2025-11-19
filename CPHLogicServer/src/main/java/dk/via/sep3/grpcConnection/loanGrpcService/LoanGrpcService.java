package dk.via.sep3.grpcConnection.loanGrpcService;

import dk.via.sep3.DTOLoan;

import java.util.List;

public interface LoanGrpcService
{
  DTOLoan createLoan(String username, String bookId, String now, String dueDate);
}

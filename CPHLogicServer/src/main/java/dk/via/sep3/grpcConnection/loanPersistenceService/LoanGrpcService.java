package dk.via.sep3.grpcConnection.loanPersistenceService;

import dk.via.sep3.DTOLoan;

public interface LoanGrpcService
{
  DTOLoan createLoan(String username, String bookId, String now, String dueDate);
}

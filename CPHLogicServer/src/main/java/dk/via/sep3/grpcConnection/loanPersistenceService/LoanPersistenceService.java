package dk.via.sep3.grpcConnection.loanPersistenceService;

import dk.via.sep3.DTOLoan;

import java.util.List;

public interface LoanPersistenceService
{
    DTOLoan createLoan(String username, String bookId, String now, String dueDate);
    List<DTOLoan> getLoansByISBN(String isbn);
}
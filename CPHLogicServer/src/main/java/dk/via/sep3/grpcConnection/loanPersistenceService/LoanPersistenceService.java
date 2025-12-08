package dk.via.sep3.grpcConnection.loanPersistenceService;

import dk.via.sep3.shared.loan.LoanDTO;

import java.util.List;

public interface LoanPersistenceService
{
    LoanDTO createLoan(String username, String bookId, String now, String dueDate);
    List<LoanDTO> getLoansByISBN(String isbn);
}
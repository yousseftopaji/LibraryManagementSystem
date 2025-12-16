package dk.via.sep3.model.loans;

import dk.via.sep3.model.domain.Loan;
import java.util.List;

public interface LoanService
{
  Loan createLoan(Loan loan);
  void extendLoan(Loan loan);
  List<Loan> getActiveLoansByUsername(String username);
}

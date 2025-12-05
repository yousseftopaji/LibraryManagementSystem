package dk.via.sep3.model.loans;

import dk.via.sep3.model.domain.Loan;
import dk.via.sep3.shared.loan.CreateLoanDTO;
import dk.via.sep3.shared.loan.LoanDTO;

public interface LoanService
{
  Loan createLoan(Loan loan);
  void extendLoan(int loanId);
}

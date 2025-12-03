package dk.via.sep3.model.loans;

import dk.via.sep3.shared.loan.CreateLoanDTO;
import dk.via.sep3.shared.loan.LoanDTO;

public interface LoanService
{
  LoanDTO createLoan(CreateLoanDTO createLoanDTO);
  void extendLoan(int loanId, String username);
}

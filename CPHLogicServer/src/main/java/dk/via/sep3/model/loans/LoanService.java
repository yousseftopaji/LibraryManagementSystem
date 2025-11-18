package dk.via.sep3.model.loans;

import dk.via.sep3.shared.CreateLoanDTO;
import dk.via.sep3.shared.LoanDTO;

public interface LoanService
{
  LoanDTO createLoan(CreateLoanDTO createLoanDTO);
}

package dk.via.sep3.model;

import dk.via.sep3.DTOLoan;

public interface LoanService
{
  DTOLoan createLoan(String username, String bookId, int loanDurationDays);
}

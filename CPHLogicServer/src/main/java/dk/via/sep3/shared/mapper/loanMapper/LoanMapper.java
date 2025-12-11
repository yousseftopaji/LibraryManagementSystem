package dk.via.sep3.shared.mapper.loanMapper;

import dk.via.sep3.DTOLoan;
import dk.via.sep3.model.domain.Loan;
import dk.via.sep3.shared.extension.CreateExtensionDTO;
import dk.via.sep3.shared.loan.CreateLoanDTO;
import dk.via.sep3.shared.loan.LoanDTO;

public interface LoanMapper
{
  Loan mapCreateLoanDTOToDomain(CreateLoanDTO createLoanDTO);

  DTOLoan mapDomainToDTOLoan(Loan loan);

  Loan mapDTOLoanToDomain(DTOLoan dtoLoan);

  LoanDTO mapDomainToLoanDTO(Loan loan);

  Loan mapCreateExtensionDTOToDomain(CreateExtensionDTO createExtensionDTO);
}

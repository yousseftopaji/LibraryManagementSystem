package dk.via.sep3.mapper.loanMapper;

import dk.via.sep3.DTOLoan;
import dk.via.sep3.application.domain.Loan;
import dk.via.sep3.DTOs.extension.CreateExtensionDTO;
import dk.via.sep3.DTOs.loan.CreateLoanDTO;
import dk.via.sep3.DTOs.loan.LoanDTO;

public interface LoanMapper
{
  Loan mapCreateLoanDTOToDomain(CreateLoanDTO createLoanDTO);

  DTOLoan mapDomainToDTOLoan(Loan loan);

  Loan mapDTOLoanToDomain(DTOLoan dtoLoan);

  LoanDTO mapDomainToLoanDTO(Loan loan);

  Loan mapCreateExtensionDTOToDomain(CreateExtensionDTO createExtensionDTO);
}

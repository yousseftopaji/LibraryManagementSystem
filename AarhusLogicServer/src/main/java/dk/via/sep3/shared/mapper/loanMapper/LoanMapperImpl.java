package dk.via.sep3.shared.mapper.loanMapper;

import dk.via.sep3.DTOLoan;
import dk.via.sep3.model.domain.Loan;
import dk.via.sep3.shared.extension.CreateExtensionDTO;
import dk.via.sep3.shared.loan.CreateLoanDTO;
import dk.via.sep3.shared.loan.LoanDTO;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Component
public class LoanMapperImpl implements LoanMapper
{
  @Override public Loan mapCreateLoanDTOToDomain(CreateLoanDTO createLoanDTO)
  {
    Loan loan = new Loan();
    loan.setUsername(createLoanDTO.getUsername());
    loan.setBookISBN(createLoanDTO.getBookISBN());
    return loan;
  }

  @Override public DTOLoan mapDomainToDTOLoan(Loan loan)
  {
    return DTOLoan.newBuilder()
        .setId(loan.getLoanId())
        .setBorrowDate(loan.getBorrowDate().toString())
        .setDueDate(loan.getDueDate().toString())
        .setUsername(loan.getUsername())
        .setIsReturned(loan.isReturned())
        .setBookId(loan.getBookId())
        .setNumberOfExtensions(loan.getNumberOfExtensions())
        .build();
  }

  @Override public Loan mapDTOLoanToDomain(DTOLoan dtoLoan)
  {
    Date borrowDate = Date.valueOf(dtoLoan.getBorrowDate());
    Date dueDate = Date.valueOf(dtoLoan.getDueDate());
    Loan loan = new Loan();
    loan.setLoanId(dtoLoan.getId());
    loan.setBorrowDate(borrowDate);
    loan.setDueDate(dueDate);
    loan.setUsername(dtoLoan.getUsername());
    loan.setReturned(dtoLoan.getIsReturned());
    loan.setBookId(dtoLoan.getBookId());
    loan.setNumberOfExtensions(dtoLoan.getNumberOfExtensions());
    return loan;
  }

  @Override public LoanDTO mapDomainToLoanDTO(Loan loan)
  {
    return new LoanDTO(
        String.valueOf(loan.getLoanId()),
        loan.getBorrowDate().toString(),
        loan.getDueDate().toString(),
        loan.isReturned(),
        loan.getNumberOfExtensions(),
        loan.getUsername(),
        loan.getBookId()
    );
  }

  @Override public Loan mapCreateExtensionDTOToDomain(CreateExtensionDTO  createExtensionDTO){
      Loan loan =  new Loan();
      loan.setLoanId(createExtensionDTO.getLoanId());
      loan.setUsername(createExtensionDTO.getUsername());
      return loan;
  }
}

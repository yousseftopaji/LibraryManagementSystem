package dk.via.sep3.mapper.loanMapper;

import dk.via.sep3.DTOLoan;
import dk.via.sep3.application.domain.Loan;
import dk.via.sep3.DTOs.extension.CreateExtensionDTO;
import dk.via.sep3.DTOs.loan.CreateLoanDTO;
import dk.via.sep3.DTOs.loan.LoanDTO;
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
    String borrowDateStr = loan.getBorrowDate() != null ? loan.getBorrowDate().toString() : null;
    String dueDateStr = loan.getDueDate() != null ? loan.getDueDate().toString() : null;

    return DTOLoan.newBuilder()
        .setId(loan.getLoanId())
        .setBorrowDate(borrowDateStr)
        .setDueDate(dueDateStr)
        .setUsername(loan.getUsername())
        .setIsReturned(loan.isReturned())
        .setBookId(loan.getBookId())
        .setNumberOfExtensions(loan.getNumberOfExtensions())
        .build();
  }

  @Override public Loan mapDTOLoanToDomain(DTOLoan dtoLoan)
  {
    Date borrowDate = !dtoLoan.getBorrowDate().isEmpty()
        ? Date.valueOf(dtoLoan.getBorrowDate()) : null;
    Date dueDate = !dtoLoan.getDueDate().isEmpty()
        ? Date.valueOf(dtoLoan.getDueDate()) : null;
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
    String borrowDateStr = loan.getBorrowDate() != null ? loan.getBorrowDate().toString() : null;
    String dueDateStr = loan.getDueDate() != null ? loan.getDueDate().toString() : null;

    return new LoanDTO(
        String.valueOf(loan.getLoanId()),
        borrowDateStr,
        dueDateStr,
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

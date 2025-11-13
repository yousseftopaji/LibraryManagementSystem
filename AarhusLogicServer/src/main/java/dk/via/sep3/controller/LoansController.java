package dk.via.sep3.controller;

import dk.via.sep3.model.LoanService;
import dk.via.sep3.model.entities.CreateLoanRequest;
import dk.via.sep3.model.entities.LoanDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loans")
public class LoansController
{
  private final LoanService loanService;

  public LoansController(LoanService loanService)
  {
    this.loanService = loanService;
  }

  @PostMapping
  public ResponseEntity<LoanDTO> createLoan(@RequestBody CreateLoanRequest request)
  {
    var grpcLoan = loanService.createLoan(
        request.getUsername(),
        request.getBookId(),
        request.getLoanDurationDays()
    );

    if (grpcLoan == null || grpcLoan.getId().isEmpty())
    {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    LoanDTO loanDTO = new LoanDTO(
        grpcLoan.getId(),
        grpcLoan.getBorrowDate(),
        grpcLoan.getDueDate(),
        grpcLoan.getIsReturned(),
        grpcLoan.getNumberOfExtensions(),
        grpcLoan.getUsername(),
        grpcLoan.getBookId()
    );

    return new ResponseEntity<>(loanDTO, HttpStatus.CREATED);
  }
}


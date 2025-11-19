package dk.via.sep3.controller;

import dk.via.sep3.model.loans.LoanService;
import dk.via.sep3.shared.loan.CreateLoanDTO;
import dk.via.sep3.shared.loan.LoanDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  public ResponseEntity<LoanDTO> createLoan(@RequestBody CreateLoanDTO request)
  {
    try
    {
      LoanDTO loanDTO = loanService.createLoan(request);

      return new ResponseEntity<>(loanDTO, HttpStatus.CREATED);
    }
    catch (IllegalArgumentException e)
    {
      // Validation failed (invalid user, book not found, invalid dates)
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    catch (IllegalStateException e)
    {
      // Book not available
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
    catch (Exception e)
    {
      // Unexpected error
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}


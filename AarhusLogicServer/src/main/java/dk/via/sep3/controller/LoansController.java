package dk.via.sep3.controller;

import dk.via.sep3.model.loans.LoanService;
import dk.via.sep3.shared.book.BookDTO;
import dk.via.sep3.shared.loan.CreateLoanDTO;
import dk.via.sep3.shared.loan.LoanDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/loans") public class LoansController
{
  private final LoanService loanService;

  public LoansController(LoanService loanService)
  {
    this.loanService = loanService;
  }

  @PostMapping public ResponseEntity<LoanDTO> createLoan(
      @RequestBody CreateLoanDTO request)
  {
    LoanDTO loanDTO = loanService.createLoan(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(loanDTO);
  }

  @PutMapping("/{id}/extensions") public ResponseEntity<Void> extendLoan(
      @PathVariable String id)
  {
    // Implementation for extending a loan would go here
    return ResponseEntity.ok().build();
  }
}


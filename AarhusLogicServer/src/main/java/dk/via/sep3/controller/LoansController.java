package dk.via.sep3.controller;

import dk.via.sep3.model.loans.LoanService;
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

  @PatchMapping("/{bookId}/extensions")
  public ResponseEntity<Void> extendLoan(
      @PathVariable String bookId)
  {
    try
    {
      loanService.extendLoan(Integer.parseInt(bookId), Integer.parseInt(bookId));
    }
    catch (Exception e)
    {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    return ResponseEntity.ok().build();
  }
}
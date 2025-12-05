package dk.via.sep3.controller;

import dk.via.sep3.model.domain.Loan;
import dk.via.sep3.model.loans.LoanService;
import dk.via.sep3.shared.loan.CreateLoanDTO;
import dk.via.sep3.shared.loan.LoanDTO;
import dk.via.sep3.shared.mapper.loanMapper.LoanMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/loans") public class LoansController
{
  private final LoanService loanService;
  private final LoanMapper loanMapper;

  public LoansController(LoanService loanService, LoanMapper loanMapper)
  {
    this.loanService = loanService;
    this.loanMapper = loanMapper;
  }

  @PostMapping
//  @PreAuthorize("permitAll()")
  public ResponseEntity<LoanDTO> createLoan(
      @RequestBody CreateLoanDTO request)
  {
    System.out.println(
        "Received loan creation request for user: " + request.getUsername()
            + " and book ISBN: " + request.getBookISBN());
    Loan loan = loanMapper.mapCreateLoanDTOToDomain(request);
    Loan createdLoan = loanService.createLoan(loan);
    LoanDTO loanDTO = loanMapper.mapDomainToLoanDTO(createdLoan);

    return new ResponseEntity<>(loanDTO, HttpStatus.CREATED);
  }

  @PatchMapping("/{id}") public ResponseEntity<Void> extendLoan(
      @PathVariable String id)
  {
    int loanId = Integer.parseInt(id);
    loanService.extendLoan(loanId);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
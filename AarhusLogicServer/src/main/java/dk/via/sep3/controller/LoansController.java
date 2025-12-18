package dk.via.sep3.controller;

import dk.via.sep3.mapper.loanMapper.LoanMapper;
import dk.via.sep3.application.domain.Loan;
import dk.via.sep3.application.services.loans.LoanService;
import dk.via.sep3.DTOs.extension.CreateExtensionDTO;
import dk.via.sep3.DTOs.loan.CreateLoanDTO;
import dk.via.sep3.DTOs.loan.LoanDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController @RequestMapping("/loans") public class LoansController
{
  private static final Logger logger = LoggerFactory.getLogger(LoansController.class);
  private final LoanService loanService;
  private final LoanMapper loanMapper;

  public LoansController(LoanService loanService, LoanMapper loanMapper)
  {
    this.loanService = loanService;
    this.loanMapper = loanMapper;
  }

  @PreAuthorize("hasRole('READER')")
  @PostMapping
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

  @PreAuthorize("hasRole('READER')")
  @PatchMapping("/extensions") public ResponseEntity<Void> extendLoan(
          @RequestBody CreateExtensionDTO request)
  {
     Loan loan = loanMapper.mapCreateExtensionDTOToDomain(request) ;
    loanService.extendLoan(loan);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PreAuthorize("hasRole('READER')")
  @GetMapping("/active")
  public ResponseEntity<List<LoanDTO>> getActiveLoansByUsername(@RequestParam String username)
  {
    logger.info("Fetching active loans for user: {}", username);

    List<Loan> activeLoans = loanService.getActiveLoansByUsername(username);
    List<LoanDTO> loanDTOs = new ArrayList<>();
    for (Loan loan : activeLoans)
    {
      LoanDTO loanDTO = loanMapper.mapDomainToLoanDTO(loan);
      loanDTOs.add(loanDTO);
    }

    logger.info("Found {} active loans for user: {}", loanDTOs.size(), username);
    return new ResponseEntity<>(loanDTOs, HttpStatus.OK);
  }
}
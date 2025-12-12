package dk.via.sep3.controller;

import dk.via.sep3.model.domain.Loan;
import dk.via.sep3.model.loans.LoanService;
import dk.via.sep3.security.JwtTokenProvider;
import dk.via.sep3.shared.extension.CreateExtensionDTO;
import dk.via.sep3.shared.loan.CreateLoanDTO;
import dk.via.sep3.shared.loan.LoanDTO;
import dk.via.sep3.shared.mapper.loanMapper.LoanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loans")
public class LoansController
{
  private static final Logger logger = LoggerFactory.getLogger(LoansController.class);
  private final LoanService loanService;
  private final LoanMapper loanMapper;
  private final JwtTokenProvider jwtTokenProvider;

  public LoansController(LoanService loanService, LoanMapper loanMapper, JwtTokenProvider jwtTokenProvider)
  {
    this.loanService = loanService;
    this.loanMapper = loanMapper;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @PreAuthorize("hasRole('Reader')")
  @PostMapping
  public ResponseEntity<LoanDTO> createLoan(@RequestBody CreateLoanDTO request)
  {
    logger.info("Creating loan for user: {} and book ISBN: {}",
        request.getUsername(), request.getBookISBN());

    Loan loan = loanMapper.mapCreateLoanDTOToDomain(request);
    Loan createdLoan = loanService.createLoan(loan);
    LoanDTO loanDTO = loanMapper.mapDomainToLoanDTO(createdLoan);

    logger.info("Loan created successfully with ID: {}", loanDTO.getId());
    return new ResponseEntity<>(loanDTO, HttpStatus.CREATED);
  }

  @PreAuthorize("hasRole('READER')")
  @PatchMapping("/extensions")
  public ResponseEntity extendLoan(
      @PathVariable String id,
      @RequestBody CreateExtensionDTO request)
  {
    logger.info("Extension request for loan ID: {} by user: {}",
        id, request.getUsername());

    // Set the loan ID from path variable
    request.setLoanId(Integer.parseInt(id));

    // Map DTO to domain
    Loan loan = loanMapper.mapCreateExtensionDTOToDomain(request);

    // Extend the loan
    loanService.extendLoan(loan);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
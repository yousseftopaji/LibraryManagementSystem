package dk.via.sep3.controller;

import dk.via.sep3.mapper.loanMapper.LoanMapper;
import dk.via.sep3.model.domain.Loan;
import dk.via.sep3.model.loans.LoanService;
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

/**
 * Controller exposing loan lifecycle endpoints.
 *
 * <p>Responsible for creating loans, extending due dates, closing loans and listing active loans.
 */
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

  /**
   * Create a new loan for a user and book specified in the request DTO.
   *
   * @param request the {@link CreateLoanDTO} containing username and book ISBN; must not be null
   * @return the created {@link LoanDTO} representing the persisted loan
   * @throws IllegalStateException or BusinessRuleViolationException if creation rules are violated
   */
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

  /**
   * Request an extension for the loan described in the DTO. The service will validate
   * ownership, timing and extension limits.
   *
   * @param request the extension details as {@link CreateExtensionDTO}; must not be null
   * @return HTTP 200 OK on success
   * @throws IllegalStateException if extension is not permitted
   */
  @PreAuthorize("hasRole('READER')")
  @PatchMapping("/extensions") public ResponseEntity<Void> extendLoan(
          @RequestBody CreateExtensionDTO request)
  {
     Loan loan = loanMapper.mapCreateExtensionDTOToDomain(request) ;
    loanService.extendLoan(loan);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Fetch active (non-returned) loans for the given username.
   *
   * @param username the username to query for; must not be null or empty
   * @return list of {@link LoanDTO} representing active loans; may throw if none found
   */
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
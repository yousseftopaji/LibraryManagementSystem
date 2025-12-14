package dk.via.sep3.controller;

import dk.via.sep3.model.domain.Loan;
import dk.via.sep3.model.loans.LoanService;
import dk.via.sep3.security.JwtTokenProvider;
import dk.via.sep3.shared.extension.CreateExtensionDTO;
import dk.via.sep3.shared.loan.CreateLoanDTO;
import dk.via.sep3.shared.loan.LoanDTO;
import dk.via.sep3.shared.mapper.loanMapper.LoanMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoansControllerTest {

  private LoanService loanService;
  private LoanMapper loanMapper;
  private JwtTokenProvider jwtTokenProvider;
  private LoansController controller;

  @BeforeEach
  void setup() {
    loanService = mock(LoanService.class);
    loanMapper = mock(LoanMapper.class);
    jwtTokenProvider = mock(JwtTokenProvider.class);

    controller = new LoansController(loanService, loanMapper, jwtTokenProvider);
  }


  // TEST: createLoan()


  @Test
  void createLoan_returnsCreatedLoanDTO() {
    CreateLoanDTO request = new CreateLoanDTO("john", "123");

    Loan domainLoan = new Loan();
    Loan createdLoan = new Loan();
    createdLoan.setLoanId(10);

    LoanDTO loanDTO = new LoanDTO();
    loanDTO.setId("10");

    when(loanMapper.mapCreateLoanDTOToDomain(request)).thenReturn(domainLoan);
    when(loanService.createLoan(domainLoan)).thenReturn(createdLoan);
    when(loanMapper.mapDomainToLoanDTO(createdLoan)).thenReturn(loanDTO);

    ResponseEntity<LoanDTO> response = controller.createLoan(request);

    assertEquals(201, response.getStatusCode().value());
    assertEquals("10", response.getBody().getId()); // now correct type

    verify(loanMapper).mapCreateLoanDTOToDomain(request);
    verify(loanService).createLoan(domainLoan);
    verify(loanMapper).mapDomainToLoanDTO(createdLoan);
  }


  // TEST: extendLoan()


  @Test
  void extendLoan_callsServiceAndReturnsHttpOk() {

    String id = "5";

    CreateExtensionDTO request = new CreateExtensionDTO();
    request.setUsername("john");

    Loan mappedLoan = new Loan();

    when(loanMapper.mapCreateExtensionDTOToDomain(request)).thenReturn(mappedLoan);

    ResponseEntity response = controller.extendLoan(id, request);

    assertEquals(200, response.getStatusCode().value());
    assertEquals(5, request.getLoanId());

    verify(loanMapper).mapCreateExtensionDTOToDomain(request);
    verify(loanService).extendLoan(mappedLoan);
  }
}


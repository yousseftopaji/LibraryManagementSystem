package dk.via.sep3.controller;

import dk.via.sep3.application.domain.Loan;
import dk.via.sep3.application.services.loans.LoanService;
import dk.via.sep3.DTOs.extension.CreateExtensionDTO;
import dk.via.sep3.DTOs.loan.CreateLoanDTO;
import dk.via.sep3.DTOs.loan.LoanDTO;
import dk.via.sep3.mapper.loanMapper.LoanMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoansControllerTest {

  private LoanService loanService;
  private LoanMapper loanMapper;
  private LoansController controller;

  private Loan loan;
  private LoanDTO loanDTO;
  private CreateLoanDTO createLoanDTO;
  private CreateExtensionDTO createExtensionDTO;

  @BeforeEach
  void setup() {
    loanService = mock(LoanService.class);
    loanMapper = mock(LoanMapper.class);
    controller = new LoansController(loanService, loanMapper);

    loan = mock(Loan.class);

    loanDTO = new LoanDTO();
    loanDTO.setId("1");

    createLoanDTO = new CreateLoanDTO(
        "testUser",
        "123"
    );

    createExtensionDTO = new CreateExtensionDTO();
    createExtensionDTO.setUsername("testUser");
  }

  @Test
  void createLoan_returnsCreatedLoanDTO() {
    when(loanMapper.mapCreateLoanDTOToDomain(createLoanDTO)).thenReturn(loan);
    when(loanService.createLoan(loan)).thenReturn(loan);
    when(loanMapper.mapDomainToLoanDTO(loan)).thenReturn(loanDTO);

    ResponseEntity<LoanDTO> response = controller.createLoan(createLoanDTO);

    assertEquals(201, response.getStatusCode().value());
    assertEquals("1", response.getBody().getId());

    verify(loanMapper).mapCreateLoanDTOToDomain(createLoanDTO);
    verify(loanService).createLoan(loan);
    verify(loanMapper).mapDomainToLoanDTO(loan);
  }

  @Test
  void extendLoan_returnsOkStatus() {
    when(loanMapper.mapCreateExtensionDTOToDomain(createExtensionDTO))
        .thenReturn(loan);

    ResponseEntity<Void> response = controller.extendLoan(createExtensionDTO);

    assertEquals(200, response.getStatusCode().value());

    verify(loanMapper).mapCreateExtensionDTOToDomain(createExtensionDTO);
    verify(loanService).extendLoan(loan);
  }

  @Test
  void getActiveLoansByUsername_returnsLoanDTOs() {
    when(loanService.getActiveLoansByUsername("testUser"))
        .thenReturn(List.of(loan));
    when(loanMapper.mapDomainToLoanDTO(loan))
        .thenReturn(loanDTO);

    ResponseEntity<List<LoanDTO>> response =
        controller.getActiveLoansByUsername("testUser");

    assertEquals(200, response.getStatusCode().value());
    assertEquals(1, response.getBody().size());
    assertEquals("1", response.getBody().get(0).getId());

    verify(loanService).getActiveLoansByUsername("testUser");
    verify(loanMapper).mapDomainToLoanDTO(loan);
  }
}

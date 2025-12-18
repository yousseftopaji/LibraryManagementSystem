package dk.via.sep3.mapper.loanMapper;

import dk.via.sep3.DTOLoan;
import dk.via.sep3.application.domain.Loan;
import dk.via.sep3.DTOs.extension.CreateExtensionDTO;
import dk.via.sep3.DTOs.loan.CreateLoanDTO;
import dk.via.sep3.DTOs.loan.LoanDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class LoanMapperImplTest {

  private LoanMapperImpl mapper;

  @BeforeEach
  void setUp() {
    mapper = new LoanMapperImpl();
  }

  // --------------------------------------------------
  // mapCreateLoanDTOToDomain
  // --------------------------------------------------

  @Test
  void mapCreateLoanDTOToDomain_mapsFieldsCorrectly() {
    CreateLoanDTO dto = new CreateLoanDTO("john", "ISBN123");

    Loan loan = mapper.mapCreateLoanDTOToDomain(dto);

    assertEquals("john", loan.getUsername());
    assertEquals("ISBN123", loan.getBookISBN());
  }

  // --------------------------------------------------
  // mapCreateExtensionDTOToDomain
  // --------------------------------------------------

  @Test
  void mapCreateExtensionDTOToDomain_mapsFieldsCorrectly() {
    CreateExtensionDTO dto = new CreateExtensionDTO();
    dto.setLoanId(10);
    dto.setUsername("alice");

    Loan loan = mapper.mapCreateExtensionDTOToDomain(dto);

    assertEquals(10, loan.getLoanId());
    assertEquals("alice", loan.getUsername());
  }

  // --------------------------------------------------
  // mapDomainToDTOLoan
  // --------------------------------------------------

  @Test
  void mapDomainToDTOLoan_mapsAllFieldsCorrectly() {
    Loan loan = new Loan();
    loan.setLoanId(5);
    loan.setUsername("bob");
    loan.setBookId(99);
    loan.setBorrowDate(Date.valueOf("2025-01-01"));
    loan.setDueDate(Date.valueOf("2025-01-31"));
    loan.setReturned(false);
    loan.setNumberOfExtensions(2);

    DTOLoan dtoLoan = mapper.mapDomainToDTOLoan(loan);

    assertEquals(5, dtoLoan.getId());
    assertEquals("2025-01-01", dtoLoan.getBorrowDate());
    assertEquals("2025-01-31", dtoLoan.getDueDate());
    assertEquals("bob", dtoLoan.getUsername());
    assertFalse(dtoLoan.getIsReturned());
    assertEquals(99, dtoLoan.getBookId());
    assertEquals(2, dtoLoan.getNumberOfExtensions());
  }

  // --------------------------------------------------
  // mapDTOLoanToDomain
  // --------------------------------------------------

  @Test
  void mapDTOLoanToDomain_mapsAllFieldsCorrectly() {
    DTOLoan dtoLoan = DTOLoan.newBuilder()
        .setId(7)
        .setBorrowDate("2025-02-01")
        .setDueDate("2025-03-03")
        .setUsername("charlie")
        .setIsReturned(true)
        .setBookId(55)
        .setNumberOfExtensions(1)
        .build();

    Loan loan = mapper.mapDTOLoanToDomain(dtoLoan);

    assertEquals(7, loan.getLoanId());
    assertEquals(Date.valueOf("2025-02-01"), loan.getBorrowDate());
    assertEquals(Date.valueOf("2025-03-03"), loan.getDueDate());
    assertEquals("charlie", loan.getUsername());
    assertTrue(loan.isReturned());
    assertEquals(55, loan.getBookId());
    assertEquals(1, loan.getNumberOfExtensions());
  }

  // --------------------------------------------------
  // mapDomainToLoanDTO
  // --------------------------------------------------

  @Test
  void mapDomainToLoanDTO_mapsAllFieldsCorrectly() {
    Loan loan = new Loan();
    loan.setLoanId(20);
    loan.setBorrowDate(Date.valueOf("2025-04-01"));
    loan.setDueDate(Date.valueOf("2025-05-01"));
    loan.setReturned(false);
    loan.setNumberOfExtensions(0);
    loan.setUsername("david");
    loan.setBookId(77);

    LoanDTO loanDTO = mapper.mapDomainToLoanDTO(loan);

    assertEquals("20", loanDTO.getId()); // String conversion
    assertEquals("2025-04-01", loanDTO.getBorrowDate());
    assertEquals("2025-05-01", loanDTO.getDueDate());
    assertFalse(loanDTO.isReturned());
    assertEquals(0, loanDTO.getNumberOfExtensions());
    assertEquals("david", loanDTO.getUsername());
    assertEquals(77, loanDTO.getBookId());
  }
}

package dk.via.sep3.controller;

import dk.via.sep3.mapper.loanMapper.LoanMapper;
import dk.via.sep3.application.domain.Loan;
import dk.via.sep3.application.services.loans.LoanService;
import dk.via.sep3.DTOs.extension.CreateExtensionDTO;
import dk.via.sep3.DTOs.loan.CreateLoanDTO;
import dk.via.sep3.DTOs.loan.LoanDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LoansController
 * Tests loan creation, extension, and retrieval functionality
 */
@ExtendWith(MockitoExtension.class)
class LoansControllerTest {

    @Mock
    private LoanService loanService;

    @Mock
    private LoanMapper loanMapper;

    @InjectMocks
    private LoansController loansController;

    private CreateLoanDTO createLoanDTO;
    private Loan domainLoan;
    private Loan createdLoan;
    private LoanDTO loanDTO;
    private CreateExtensionDTO createExtensionDTO;
    private Loan extensionDomainLoan;

    @BeforeEach
    void setUp() {
        // Setup CreateLoanDTO
        createLoanDTO = new CreateLoanDTO("testuser", "1234567890");

        // Setup domain loan (before creation)
        domainLoan = new Loan();
        domainLoan.setUsername("testuser");
        domainLoan.setBookId(1);
        domainLoan.setBorrowDate(Date.valueOf(LocalDate.now()));
        domainLoan.setDueDate(Date.valueOf(LocalDate.now().plusDays(30)));

        // Setup created loan (after creation)
        createdLoan = new Loan();
        createdLoan.setLoanId(1);
        createdLoan.setUsername("testuser");
        createdLoan.setBookId(1);
        createdLoan.setBorrowDate(Date.valueOf(LocalDate.now()));
        createdLoan.setDueDate(Date.valueOf(LocalDate.now().plusDays(30)));
        createdLoan.setReturned(false);
        createdLoan.setNumberOfExtensions(0);

        // Setup LoanDTO
        loanDTO = new LoanDTO();
        loanDTO.setId("1");
        loanDTO.setUsername("testuser");
        loanDTO.setBookId(1);
        loanDTO.setBorrowDate(LocalDate.now().toString());
        loanDTO.setDueDate(LocalDate.now().plusDays(30).toString());
        loanDTO.setReturned(false);
        loanDTO.setNumberOfExtensions(0);

        // Setup CreateExtensionDTO
        createExtensionDTO = new CreateExtensionDTO();
        createExtensionDTO.setLoanId(1);
        createExtensionDTO.setUsername("testuser");

        // Setup extension domain loan
        extensionDomainLoan = new Loan();
        extensionDomainLoan.setLoanId(1);
        extensionDomainLoan.setUsername("testuser");
    }

    @Test
    @DisplayName("Should successfully create loan when valid request is provided")
    void testCreateLoan_Success() {
        // Arrange
        when(loanMapper.mapCreateLoanDTOToDomain(createLoanDTO)).thenReturn(domainLoan);
        when(loanService.createLoan(domainLoan)).thenReturn(createdLoan);
        when(loanMapper.mapDomainToLoanDTO(createdLoan)).thenReturn(loanDTO);

        // Act
        ResponseEntity<LoanDTO> response = loansController.createLoan(createLoanDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("1", response.getBody().getId());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals(1, response.getBody().getBookId());
        assertFalse(response.getBody().isReturned());
        assertEquals(0, response.getBody().getNumberOfExtensions());

        // Verify interactions
        verify(loanMapper, times(1)).mapCreateLoanDTOToDomain(createLoanDTO);
        verify(loanService, times(1)).createLoan(domainLoan);
        verify(loanMapper, times(1)).mapDomainToLoanDTO(createdLoan);
    }

    @Test
    @DisplayName("Should throw exception when creating loan with invalid username")
    void testCreateLoan_InvalidUsername() {
        // Arrange
        CreateLoanDTO invalidDTO = new CreateLoanDTO("", "1234567890");

        Loan invalidLoan = new Loan();
        invalidLoan.setUsername("");

        when(loanMapper.mapCreateLoanDTOToDomain(invalidDTO)).thenReturn(invalidLoan);
        when(loanService.createLoan(invalidLoan)).thenThrow(new IllegalArgumentException("Username cannot be empty"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> loansController.createLoan(invalidDTO));

        // Verify interactions
        verify(loanMapper, times(1)).mapCreateLoanDTOToDomain(invalidDTO);
        verify(loanService, times(1)).createLoan(invalidLoan);
        verify(loanMapper, never()).mapDomainToLoanDTO(any(Loan.class));
    }

    @Test
    @DisplayName("Should throw exception when creating loan with invalid ISBN")
    void testCreateLoan_InvalidISBN() {
        // Arrange
        CreateLoanDTO invalidDTO = new CreateLoanDTO("testuser", "");

        Loan invalidLoan = new Loan();
        invalidLoan.setUsername("testuser");

        when(loanMapper.mapCreateLoanDTOToDomain(invalidDTO)).thenReturn(invalidLoan);
        when(loanService.createLoan(invalidLoan)).thenThrow(new IllegalArgumentException("Book ISBN cannot be empty"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> loansController.createLoan(invalidDTO));

        // Verify interactions
        verify(loanMapper, times(1)).mapCreateLoanDTOToDomain(invalidDTO);
        verify(loanService, times(1)).createLoan(invalidLoan);
        verify(loanMapper, never()).mapDomainToLoanDTO(any(Loan.class));
    }

    @Test
    @DisplayName("Should throw exception when book is not available")
    void testCreateLoan_BookNotAvailable() {
        // Arrange
        when(loanMapper.mapCreateLoanDTOToDomain(createLoanDTO)).thenReturn(domainLoan);
        when(loanService.createLoan(domainLoan)).thenThrow(new IllegalStateException("Book is not available"));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> loansController.createLoan(createLoanDTO));

        // Verify interactions
        verify(loanMapper, times(1)).mapCreateLoanDTOToDomain(createLoanDTO);
        verify(loanService, times(1)).createLoan(domainLoan);
        verify(loanMapper, never()).mapDomainToLoanDTO(any(Loan.class));
    }

    @Test
    @DisplayName("Should successfully extend loan when valid request is provided")
    void testExtendLoan_Success() {
        // Arrange
        when(loanMapper.mapCreateExtensionDTOToDomain(createExtensionDTO)).thenReturn(extensionDomainLoan);
        doNothing().when(loanService).extendLoan(extensionDomainLoan);

        // Act
        ResponseEntity<Void> response = loansController.extendLoan(createExtensionDTO);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify interactions
        verify(loanMapper, times(1)).mapCreateExtensionDTOToDomain(createExtensionDTO);
        verify(loanService, times(1)).extendLoan(extensionDomainLoan);
    }

    @Test
    @DisplayName("Should throw exception when extending loan with invalid loan ID")
    void testExtendLoan_InvalidLoanId() {
        // Arrange
        CreateExtensionDTO invalidDTO = new CreateExtensionDTO();
        invalidDTO.setLoanId(999);
        invalidDTO.setUsername("testuser");

        Loan invalidLoan = new Loan();
        invalidLoan.setLoanId(999);
        invalidLoan.setUsername("testuser");

        when(loanMapper.mapCreateExtensionDTOToDomain(invalidDTO)).thenReturn(invalidLoan);
        doThrow(new IllegalArgumentException("Loan not found with ID: 999")).when(loanService).extendLoan(invalidLoan);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> loansController.extendLoan(invalidDTO));

        // Verify interactions
        verify(loanMapper, times(1)).mapCreateExtensionDTOToDomain(invalidDTO);
        verify(loanService, times(1)).extendLoan(invalidLoan);
    }

    @Test
    @DisplayName("Should throw exception when user is not the loan owner")
    void testExtendLoan_NotOwner() {
        // Arrange
        CreateExtensionDTO invalidDTO = new CreateExtensionDTO();
        invalidDTO.setLoanId(1);
        invalidDTO.setUsername("otheruser");

        Loan invalidLoan = new Loan();
        invalidLoan.setLoanId(1);
        invalidLoan.setUsername("otheruser");

        when(loanMapper.mapCreateExtensionDTOToDomain(invalidDTO)).thenReturn(invalidLoan);
        doThrow(new IllegalStateException("User is not the owner of the loan")).when(loanService).extendLoan(invalidLoan);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> loansController.extendLoan(invalidDTO));

        // Verify interactions
        verify(loanMapper, times(1)).mapCreateExtensionDTOToDomain(invalidDTO);
        verify(loanService, times(1)).extendLoan(invalidLoan);
    }

    @Test
    @DisplayName("Should throw exception when maximum extensions reached")
    void testExtendLoan_MaxExtensionsReached() {
        // Arrange
        when(loanMapper.mapCreateExtensionDTOToDomain(createExtensionDTO)).thenReturn(extensionDomainLoan);
        doThrow(new IllegalStateException("Loan has reached the maximum number of extensions")).when(loanService).extendLoan(extensionDomainLoan);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> loansController.extendLoan(createExtensionDTO));

        // Verify interactions
        verify(loanMapper, times(1)).mapCreateExtensionDTOToDomain(createExtensionDTO);
        verify(loanService, times(1)).extendLoan(extensionDomainLoan);
    }

    @Test
    @DisplayName("Should return active loans for user when username is valid")
    void testGetActiveLoansByUsername_Success() {
        // Arrange
        String username = "testuser";
        Loan loan1 = new Loan();
        loan1.setLoanId(1);
        loan1.setUsername(username);
        loan1.setBookId(1);
        loan1.setReturned(false);

        Loan loan2 = new Loan();
        loan2.setLoanId(2);
        loan2.setUsername(username);
        loan2.setBookId(2);
        loan2.setReturned(false);

        List<Loan> activeLoans = Arrays.asList(loan1, loan2);

        LoanDTO loanDTO1 = new LoanDTO();
        loanDTO1.setId("1");
        loanDTO1.setUsername(username);
        loanDTO1.setBookId(1);

        LoanDTO loanDTO2 = new LoanDTO();
        loanDTO2.setId("2");
        loanDTO2.setUsername(username);
        loanDTO2.setBookId(2);

        when(loanService.getActiveLoansByUsername(username)).thenReturn(activeLoans);
        when(loanMapper.mapDomainToLoanDTO(loan1)).thenReturn(loanDTO1);
        when(loanMapper.mapDomainToLoanDTO(loan2)).thenReturn(loanDTO2);

        // Act
        ResponseEntity<List<LoanDTO>> response = loansController.getActiveLoansByUsername(username);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("1", response.getBody().get(0).getId());
        assertEquals("2", response.getBody().get(1).getId());

        // Verify interactions
        verify(loanService, times(1)).getActiveLoansByUsername(username);
        verify(loanMapper, times(1)).mapDomainToLoanDTO(loan1);
        verify(loanMapper, times(1)).mapDomainToLoanDTO(loan2);
    }

    @Test
    @DisplayName("Should return empty list when user has no active loans")
    void testGetActiveLoansByUsername_NoActiveLoans() {
        // Arrange
        String username = "testuser";
        when(loanService.getActiveLoansByUsername(username)).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<LoanDTO>> response = loansController.getActiveLoansByUsername(username);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());

        // Verify interactions
        verify(loanService, times(1)).getActiveLoansByUsername(username);
        verify(loanMapper, never()).mapDomainToLoanDTO(any(Loan.class));
    }

    @Test
    @DisplayName("Should throw exception when username is null")
    void testGetActiveLoansByUsername_NullUsername() {
        // Arrange
        when(loanService.getActiveLoansByUsername(null)).thenThrow(new IllegalArgumentException("Username cannot be null"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> loansController.getActiveLoansByUsername(null));

        // Verify interactions
        verify(loanService, times(1)).getActiveLoansByUsername(null);
        verify(loanMapper, never()).mapDomainToLoanDTO(any(Loan.class));
    }
}


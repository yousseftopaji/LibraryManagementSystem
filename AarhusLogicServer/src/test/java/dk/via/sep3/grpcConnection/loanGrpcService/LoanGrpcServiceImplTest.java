package dk.via.sep3.grpcConnection.loanGrpcService;

import dk.via.sep3.*;
import dk.via.sep3.mapper.loanMapper.LoanMapper;
import dk.via.sep3.application.domain.Loan;
import io.grpc.ManagedChannel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanGrpcServiceImplTest {

    @Mock
    private ManagedChannel channel;

    @Mock
    private LoanServiceGrpc.LoanServiceBlockingStub loanStub;

    @Mock
    private LoanMapper loanMapper;

    private LoanGrpcServiceImpl loanGrpcService;
    private MockedStatic<LoanServiceGrpc> mockedStatic;

    @BeforeEach
    void setUp() {
        mockedStatic = mockStatic(LoanServiceGrpc.class);
        mockedStatic.when(() -> LoanServiceGrpc.newBlockingStub(channel)).thenReturn(loanStub);
        loanGrpcService = new LoanGrpcServiceImpl(channel, loanMapper);
    }

    @AfterEach
    void tearDown() {
        if (mockedStatic != null) {
            mockedStatic.close();
        }
    }

    @Test
    @DisplayName("Should create loan successfully")
    void testCreateLoan_Success() {
        // Arrange
        Loan loan = new Loan();
        loan.setUsername("testuser");
        loan.setBookId(1);
        loan.setBorrowDate(Date.valueOf("2025-01-01"));
        loan.setDueDate(Date.valueOf("2025-01-31"));

        DTOLoan dtoLoan = DTOLoan.newBuilder()
            .setId(1)
            .setUsername("testuser")
            .setBookId(1)
            .build();

        CreateLoanResponse response = CreateLoanResponse.newBuilder()
            .setSuccess(true)
            .setLoan(dtoLoan)
            .build();

        when(loanStub.createLoan(any(CreateLoanRequest.class))).thenReturn(response);
        when(loanMapper.mapDTOLoanToDomain(dtoLoan)).thenReturn(loan);

        // Act
        Loan result = loanGrpcService.createLoan(loan);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(loanStub).createLoan(any(CreateLoanRequest.class));
    }

    @Test
    @DisplayName("Should return null when create loan fails")
    void testCreateLoan_Failure() {
        // Arrange
        Loan loan = new Loan();
        loan.setUsername("testuser");
        loan.setBookId(1);
        loan.setBorrowDate(Date.valueOf("2025-01-01"));
        loan.setDueDate(Date.valueOf("2025-01-31"));

        CreateLoanResponse response = CreateLoanResponse.newBuilder()
            .setSuccess(false)
            .setMessage("Failed to create loan")
            .build();

        when(loanStub.createLoan(any(CreateLoanRequest.class))).thenReturn(response);

        // Act
        Loan result = loanGrpcService.createLoan(loan);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle exception when creating loan")
    void testCreateLoan_Exception() {
        // Arrange
        Loan loan = new Loan();
        loan.setUsername("testuser");
        loan.setBookId(1);
        loan.setBorrowDate(Date.valueOf("2025-01-01"));
        loan.setDueDate(Date.valueOf("2025-01-31"));

        when(loanStub.createLoan(any(CreateLoanRequest.class)))
            .thenThrow(new RuntimeException("gRPC error"));

        // Act
        Loan result = loanGrpcService.createLoan(loan);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Should get loans by ISBN successfully")
    void testGetLoansByISBN_Success() {
        // Arrange
        String isbn = "123456";
        DTOLoan dtoLoan1 = DTOLoan.newBuilder().setId(1).setUsername("user1").build();
        DTOLoan dtoLoan2 = DTOLoan.newBuilder().setId(2).setUsername("user2").build();

        GetLoansByISBNResponse response = GetLoansByISBNResponse.newBuilder()
            .addLoans(dtoLoan1)
            .addLoans(dtoLoan2)
            .build();

        Loan loan1 = new Loan();
        loan1.setLoanId(1);
        Loan loan2 = new Loan();
        loan2.setLoanId(2);

        when(loanStub.getLoansByISBN(any(GetLoansByISBNRequest.class))).thenReturn(response);
        when(loanMapper.mapDTOLoanToDomain(dtoLoan1)).thenReturn(loan1);
        when(loanMapper.mapDTOLoanToDomain(dtoLoan2)).thenReturn(loan2);

        // Act
        List<Loan> result = loanGrpcService.getLoansByISBN(isbn);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(loanStub).getLoansByISBN(any(GetLoansByISBNRequest.class));
    }

    @Test
    @DisplayName("Should return empty list when no loans found by ISBN")
    void testGetLoansByISBN_EmptyList() {
        // Arrange
        String isbn = "123456";
        GetLoansByISBNResponse response = GetLoansByISBNResponse.newBuilder().build();

        when(loanStub.getLoansByISBN(any(GetLoansByISBNRequest.class))).thenReturn(response);

        // Act
        List<Loan> result = loanGrpcService.getLoansByISBN(isbn);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should extend loan successfully")
    void testExtendLoan_Success() {
        // Arrange
        Loan loan = new Loan();
        loan.setLoanId(1);
        loan.setUsername("testuser");

        DTOLoan dtoLoan = DTOLoan.newBuilder().setId(1).build();

        ExtendLoanResponse response = ExtendLoanResponse.newBuilder()
            .setSuccess(true)
            .build();

        when(loanMapper.mapDomainToDTOLoan(loan)).thenReturn(dtoLoan);
        when(loanStub.extendLoan(any(ExtendLoanRequest.class))).thenReturn(response);

        // Act & Assert
        assertDoesNotThrow(() -> loanGrpcService.extendLoan(loan));
        verify(loanStub).extendLoan(any(ExtendLoanRequest.class));
    }

    @Test
    @DisplayName("Should throw exception when extend loan fails")
    void testExtendLoan_Failure() {
        // Arrange
        Loan loan = new Loan();
        loan.setLoanId(1);
        loan.setUsername("testuser");

        DTOLoan dtoLoan = DTOLoan.newBuilder().setId(1).build();

        ExtendLoanResponse response = ExtendLoanResponse.newBuilder()
            .setSuccess(false)
            .setMessage("Failed to extend")
            .build();

        when(loanMapper.mapDomainToDTOLoan(loan)).thenReturn(dtoLoan);
        when(loanStub.extendLoan(any(ExtendLoanRequest.class))).thenReturn(response);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> loanGrpcService.extendLoan(loan));
    }

    @Test
    @DisplayName("Should throw exception when extend loan encounters error")
    void testExtendLoan_Exception() {
        // Arrange
        Loan loan = new Loan();
        loan.setLoanId(1);

        DTOLoan dtoLoan = DTOLoan.newBuilder().setId(1).build();

        when(loanMapper.mapDomainToDTOLoan(loan)).thenReturn(dtoLoan);
        when(loanStub.extendLoan(any(ExtendLoanRequest.class)))
            .thenThrow(new RuntimeException("gRPC error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> loanGrpcService.extendLoan(loan));
    }

    @Test
    @DisplayName("Should get loan by ID successfully")
    void testGetLoanById_Success() {
        // Arrange
        int loanId = 1;
        DTOLoan dtoLoan = DTOLoan.newBuilder()
            .setId(loanId)
            .setUsername("testuser")
            .build();

        GetLoanByIdResponse response = GetLoanByIdResponse.newBuilder()
            .setSuccess(true)
            .setLoan(dtoLoan)
            .build();

        Loan loan = new Loan();
        loan.setLoanId(loanId);

        when(loanStub.getLoanById(any(GetLoanByIdRequest.class))).thenReturn(response);
        when(loanMapper.mapDTOLoanToDomain(dtoLoan)).thenReturn(loan);

        // Act
        Loan result = loanGrpcService.getLoanById(loanId);

        // Assert
        assertNotNull(result);
        assertEquals(loanId, result.getLoanId());
        verify(loanStub).getLoanById(any(GetLoanByIdRequest.class));
    }

    @Test
    @DisplayName("Should return null when get loan by ID fails")
    void testGetLoanById_Failure() {
        // Arrange
        int loanId = 1;
        GetLoanByIdResponse response = GetLoanByIdResponse.newBuilder()
            .setSuccess(false)
            .setMessage("Loan not found")
            .build();

        when(loanStub.getLoanById(any(GetLoanByIdRequest.class))).thenReturn(response);

        // Act
        Loan result = loanGrpcService.getLoanById(loanId);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Should return null when get loan by ID encounters exception")
    void testGetLoanById_Exception() {
        // Arrange
        int loanId = 1;
        when(loanStub.getLoanById(any(GetLoanByIdRequest.class)))
            .thenThrow(new RuntimeException("gRPC error"));

        // Act
        Loan result = loanGrpcService.getLoanById(loanId);

        // Assert
        assertNull(result);
    }
}


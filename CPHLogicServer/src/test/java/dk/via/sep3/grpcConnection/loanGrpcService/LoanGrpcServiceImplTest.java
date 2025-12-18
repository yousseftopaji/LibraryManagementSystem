package dk.via.sep3.grpcConnection.loanGrpcService;

import dk.via.sep3.*;
import dk.via.sep3.application.domain.Loan;
import dk.via.sep3.exceptionHandler.GrpcCommunicationException;
import dk.via.sep3.mapper.loanMapper.LoanMapper;
import io.grpc.ManagedChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanGrpcServiceImplTest {

  private LoanServiceGrpc.LoanServiceBlockingStub loanStub;
  private LoanMapper loanMapper;
  private LoanGrpcServiceImpl service;

  @BeforeEach
  void setUp() {
    loanStub = mock(LoanServiceGrpc.LoanServiceBlockingStub.class);
    loanMapper = mock(LoanMapper.class);

    ManagedChannel channel = mock(ManagedChannel.class);
    service = new LoanGrpcServiceImpl(channel, loanMapper);

    injectStub(service, loanStub);
  }

  private void injectStub(LoanGrpcServiceImpl service,
      LoanServiceGrpc.LoanServiceBlockingStub stub) {
    try {
      var field = LoanGrpcServiceImpl.class.getDeclaredField("loanStub");
      field.setAccessible(true);
      field.set(service, stub);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // ---------------------------------------------------
  // createLoan()
  // ---------------------------------------------------

  @Test
  void createLoan_success_returnsMappedLoan() {
    Loan loan = new Loan();
    loan.setUsername("john");
    loan.setBookId(1);
    loan.setBorrowDate(Date.valueOf("2024-01-01"));
    loan.setDueDate(Date.valueOf("2024-02-01"));

    DTOLoan dtoLoan = DTOLoan.newBuilder().setId(10).build();
    CreateLoanResponse response = CreateLoanResponse.newBuilder()
        .setSuccess(true)
        .setLoan(dtoLoan)
        .build();

    when(loanStub.createLoan(any(CreateLoanRequest.class)))
        .thenReturn(response);
    when(loanMapper.mapDTOLoanToDomain(dtoLoan))
        .thenReturn(new Loan());

    Loan result = service.createLoan(loan);

    assertNotNull(result);
    verify(loanStub).createLoan(any(CreateLoanRequest.class));
    verify(loanMapper).mapDTOLoanToDomain(dtoLoan);
  }

  @Test
  void createLoan_failure_returnsNull() {
    CreateLoanResponse response = CreateLoanResponse.newBuilder()
        .setSuccess(false)
        .setMessage("error")
        .build();

    when(loanStub.createLoan(any(CreateLoanRequest.class)))
        .thenReturn(response);

    Loan result = service.createLoan(new Loan());

    assertNull(result);
  }

  // ---------------------------------------------------
  // getLoansByISBN()
  // ---------------------------------------------------

  @Test
  void getLoansByISBN_returnsMappedLoans() {
    DTOLoan dtoLoan = DTOLoan.newBuilder().setId(1).build();
    GetLoansByISBNResponse response = GetLoansByISBNResponse.newBuilder()
        .addLoans(dtoLoan)
        .build();

    when(loanStub.getLoansByISBN(any(GetLoansByISBNRequest.class)))
        .thenReturn(response);
    when(loanMapper.mapDTOLoanToDomain(dtoLoan))
        .thenReturn(new Loan());

    List<Loan> result = service.getLoansByISBN("123");

    assertEquals(1, result.size());
    verify(loanStub).getLoansByISBN(any(GetLoansByISBNRequest.class));
  }

  // ---------------------------------------------------
  // extendLoan()
  // ---------------------------------------------------

  @Test
  void extendLoan_success_executesWithoutException() {
    Loan loan = new Loan();
    loan.setLoanId(5);

    DTOLoan dtoLoan = DTOLoan.newBuilder().setId(5).build();
    ExtendLoanResponse response = ExtendLoanResponse.newBuilder()
        .setSuccess(true)
        .build();

    when(loanMapper.mapDomainToDTOLoan(loan)).thenReturn(dtoLoan);
    when(loanStub.extendLoan(any(ExtendLoanRequest.class)))
        .thenReturn(response);

    assertDoesNotThrow(() -> service.extendLoan(loan));
  }

  @Test
  void extendLoan_failure_throwsRuntimeException() {
    Loan loan = new Loan();
    loan.setLoanId(5);

    DTOLoan dtoLoan = DTOLoan.newBuilder().setId(5).build();
    ExtendLoanResponse response = ExtendLoanResponse.newBuilder()
        .setSuccess(false)
        .setMessage("fail")
        .build();

    when(loanMapper.mapDomainToDTOLoan(loan)).thenReturn(dtoLoan);
    when(loanStub.extendLoan(any(ExtendLoanRequest.class)))
        .thenReturn(response);

    assertThrows(RuntimeException.class,
        () -> service.extendLoan(loan));
  }

  // ---------------------------------------------------
  // getLoanById()
  // ---------------------------------------------------

  @Test
  void getLoanById_success_returnsMappedLoan() {
    DTOLoan dtoLoan = DTOLoan.newBuilder().setId(3).build();
    GetLoanByIdResponse response = GetLoanByIdResponse.newBuilder()
        .setSuccess(true)
        .setLoan(dtoLoan)
        .build();

    when(loanStub.getLoanById(any(GetLoanByIdRequest.class)))
        .thenReturn(response);
    when(loanMapper.mapDTOLoanToDomain(dtoLoan))
        .thenReturn(new Loan());

    Loan result = service.getLoanById(3);

    assertNotNull(result);
  }

  // ---------------------------------------------------
  // getActiveLoansByUsername()
  // ---------------------------------------------------

  @Test
  void getActiveLoansByUsername_success_returnsLoans() {
    DTOLoan dtoLoan = DTOLoan.newBuilder().setId(1).build();
    GetActiveLoansByUsernameResponse response =
        GetActiveLoansByUsernameResponse.newBuilder()
            .setSuccess(true)
            .addLoans(dtoLoan)
            .build();

    when(loanStub.getActiveLoansByUsername(any(GetActiveLoansByUsernameRequest.class)))
        .thenReturn(response);
    when(loanMapper.mapDTOLoanToDomain(dtoLoan))
        .thenReturn(new Loan());

    List<Loan> result = service.getActiveLoansByUsername("john");

    assertEquals(1, result.size());
  }

  @Test
  void getActiveLoansByUsername_exception_throwsGrpcCommunicationException() {
    when(loanStub.getActiveLoansByUsername(any(GetActiveLoansByUsernameRequest.class)))
        .thenThrow(RuntimeException.class);

    assertThrows(GrpcCommunicationException.class,
        () -> service.getActiveLoansByUsername("john"));
  }
}

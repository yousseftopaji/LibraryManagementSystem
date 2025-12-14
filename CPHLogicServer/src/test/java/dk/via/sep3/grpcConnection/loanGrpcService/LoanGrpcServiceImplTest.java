package dk.via.sep3.grpcConnection.loanGrpcService;

import dk.via.sep3.*;
import dk.via.sep3.model.domain.Loan;
import dk.via.sep3.shared.mapper.loanMapper.LoanMapper;
import io.grpc.ManagedChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanGrpcServiceImplTest {

  private LoanServiceGrpc.LoanServiceBlockingStub stub;
  private LoanMapper mapper;
  private LoanGrpcServiceImpl service;

  @BeforeEach
  void setUp() throws Exception {
    stub = mock(LoanServiceGrpc.LoanServiceBlockingStub.class);
    mapper = mock(LoanMapper.class);

    ManagedChannel channel = mock(ManagedChannel.class);
    service = new LoanGrpcServiceImpl(channel, mapper);

    // Inject mocked stub using reflection
    Field stubField = LoanGrpcServiceImpl.class.getDeclaredField("loanStub");
    stubField.setAccessible(true);
    stubField.set(service, stub);
  }


  // createLoan()


  @Test
  void createLoan_success_returnsMappedLoan() {
    Loan inputLoan = new Loan();
    inputLoan.setUsername("john");
    inputLoan.setBookId(1);
    inputLoan.setBorrowDate(Date.valueOf("2025-01-01"));
    inputLoan.setDueDate(Date.valueOf("2025-01-31"));

    DTOLoan dtoLoan = DTOLoan.newBuilder()
        .setId(10)
        .setUsername("john")
        .setBookId(1)
        .build();

    CreateLoanResponse response = CreateLoanResponse.newBuilder()
        .setLoan(dtoLoan)
        .setSuccess(true)
        .build();

    Loan mappedLoan = new Loan();
    mappedLoan.setLoanId(10);

    when(stub.createLoan(any(CreateLoanRequest.class))).thenReturn(response);
    when(mapper.mapDTOLoanToDomain(dtoLoan)).thenReturn(mappedLoan);

    Loan result = service.createLoan(inputLoan);

    assertNotNull(result);
    assertEquals(10, result.getLoanId());
  }

  @Test
  void createLoan_failure_returnsNull() {
    CreateLoanResponse response = CreateLoanResponse.newBuilder()
        .setSuccess(false)
        .setMessage("error")
        .build();

    when(stub.createLoan(any(CreateLoanRequest.class))).thenReturn(response);

    Loan result = service.createLoan(new Loan());

    assertNull(result);
  }

  @Test
  void createLoan_exception_returnsNull() {
    when(stub.createLoan(any())).thenThrow(new RuntimeException());

    Loan result = service.createLoan(new Loan());

    assertNull(result);
  }


  // getLoansByISBN()


  @Test
  void getLoansByISBN_returnsMappedLoans() {
    DTOLoan dtoLoan = DTOLoan.newBuilder()
        .setId(1)
        .setUsername("alice")
        .build();

    GetLoansByISBNResponse response = GetLoansByISBNResponse.newBuilder()
        .addLoans(dtoLoan)
        .build();

    Loan mappedLoan = new Loan();
    mappedLoan.setLoanId(1);

    when(stub.getLoansByISBN(any(GetLoansByISBNRequest.class)))
        .thenReturn(response);
    when(mapper.mapDTOLoanToDomain(dtoLoan)).thenReturn(mappedLoan);

    List<Loan> loans = service.getLoansByISBN("isbn-123");

    assertEquals(1, loans.size());
    assertEquals(1, loans.get(0).getLoanId());
  }


  // extendLoan()


  @Test
  void extendLoan_success_doesNotThrow() {
    Loan loan = new Loan();
    loan.setLoanId(5);

    DTOLoan dtoLoan = DTOLoan.newBuilder().setId(5).build();

    ExtendLoanResponse response = ExtendLoanResponse.newBuilder()
        .setSuccess(true)
        .build();

    when(mapper.mapDomainToDTOLoan(loan)).thenReturn(dtoLoan);
    when(stub.extendLoan(any(ExtendLoanRequest.class))).thenReturn(response);

    assertDoesNotThrow(() -> service.extendLoan(loan));
  }

  @Test
  void extendLoan_failure_throwsException() {
    Loan loan = new Loan();
    loan.setLoanId(5);

    DTOLoan dtoLoan = DTOLoan.newBuilder().setId(5).build();

    ExtendLoanResponse response = ExtendLoanResponse.newBuilder()
        .setSuccess(false)
        .setMessage("fail")
        .build();

    when(mapper.mapDomainToDTOLoan(loan)).thenReturn(dtoLoan);
    when(stub.extendLoan(any())).thenReturn(response);

    assertThrows(RuntimeException.class,
        () -> service.extendLoan(loan));
  }


  // getLoanById()

  @Test
  void getLoanById_success_returnsMappedLoan() {
    DTOLoan dtoLoan = DTOLoan.newBuilder().setId(7).build();

    GetLoanByIdResponse response = GetLoanByIdResponse.newBuilder()
        .setLoan(dtoLoan)
        .setSuccess(true)
        .build();

    Loan mapped = new Loan();
    mapped.setLoanId(7);

    when(stub.getLoanById(any(GetLoanByIdRequest.class)))
        .thenReturn(response);
    when(mapper.mapDTOLoanToDomain(dtoLoan)).thenReturn(mapped);

    Loan result = service.getLoanById(7);

    assertNotNull(result);
    assertEquals(7, result.getLoanId());
  }

  @Test
  void getLoanById_failure_returnsNull() {
    GetLoanByIdResponse response = GetLoanByIdResponse.newBuilder()
        .setSuccess(false)
        .build();

    when(stub.getLoanById(any())).thenReturn(response);

    Loan result = service.getLoanById(7);

    assertNull(result);
  }
}

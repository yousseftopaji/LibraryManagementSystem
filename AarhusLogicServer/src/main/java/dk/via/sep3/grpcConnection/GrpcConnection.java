package dk.via.sep3.grpcConnection;

import dk.via.sep3.*;
import dk.via.sep3.model.entities.CreateLoanDTO;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.List;

public class GrpcConnection implements GrpcConnectionInterface
{
  //I want to check my connection to this server
  private ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",
      9090).usePlaintext().build();


  private BookServiceGrpc.BookServiceBlockingStub bookStub = BookServiceGrpc.newBlockingStub(
      channel);

  private LoanServiceGrpc.LoanServiceBlockingStub loanStub = LoanServiceGrpc.newBlockingStub(
      channel);

  public List<DTOBook> getAllBooks()
  {
    try
    {
      GetAllBooksRequest request = GetAllBooksRequest.newBuilder().build();
      System.out.println("Sending gRPC request to get all books...");
      GetAllBooksResponse response = bookStub.getAllBooks(request);
      System.out.println(response.getBooksList() + " <- Received gRPC response with all books.");
      return response.getBooksList();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      return new ArrayList<>();
    }
  }

  @Override
  public DTOBook getBook(String isbn)
  {
    try
    {
      GetBookRequest request = GetBookRequest.newBuilder()
          .setIsbn(isbn)
          .build();
      System.out.println("Sending gRPC request to get book with ISBN: " + isbn);
      GetBookResponse response = bookStub.getBook(request);
      System.out.println("Received gRPC response: " + response.getBook());
      return response.getBook();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      return null;
    }
  }

  @Override
  public CreateLoanResponse createLoan(CreateLoanDTO createLoanDTO)
  {
    try
    {
      CreateLoanRequest request = CreateLoanRequest.newBuilder()
          .setBookISBN(createLoanDTO.getBookISBN())
          .setUsername(createLoanDTO.getUsername())
          .build();
      System.out.println("Sending gRPC request to create loan for ISBN: " + createLoanDTO.getBookISBN());
      CreateLoanResponse response = loanStub.createLoan(request);
      System.out.println("Received gRPC response: Loan created with ID: " + response.getLoanId());
      return response;
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      return null;
    }
  }
}

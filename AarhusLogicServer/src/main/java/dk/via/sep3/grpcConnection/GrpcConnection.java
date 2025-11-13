package dk.via.sep3.grpcConnection;

import dk.via.sep3.*;
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
  public List<DTOBook> getBookByIsbn(String isbn)
  {
    try
    {
      GetBookByIsbnRequest request = GetBookByIsbnRequest.newBuilder()
          .setIsbn(isbn)
          .build();
      System.out.println("Sending gRPC request to get book by ISBN: " + isbn);
      GetBookByIsbnResponse response = bookStub.getBookByIsbn(request);
      System.out.println("Received gRPC response: " + response.getBookList());
      return response.getBookList();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      return null;
    }
  }

  @Override
  public DTOLoan createLoan(String username, String bookId, int loanDurationDays)
  {
    try
    {
      CreateLoanRequest request = CreateLoanRequest.newBuilder()
          .setUsername(username)
          .setBookId(bookId)
          .setLoanDurationDays(loanDurationDays)
          .build();
      System.out.println("Sending gRPC request to create loan for user: " + username + ", bookId: " + bookId);
      CreateLoanResponse response = loanStub.createLoan(request);

      if (response.getSuccess())
      {
        System.out.println("Loan created successfully: " + response.getLoan());
        return response.getLoan();
      }
      else
      {
        System.err.println("Failed to create loan: " + response.getMessage());
        return null;
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      return null;
    }
  }
}

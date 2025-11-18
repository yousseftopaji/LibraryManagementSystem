package dk.via.sep3.grpcConnection;

import dk.via.sep3.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
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
  public List<DTOBook> getBooksByIsbn(String isbn)
  {
    try
    {
      GetBooksByIsbnRequest request = GetBooksByIsbnRequest.newBuilder()
          .setIsbn(isbn)
          .build();
      System.out.println("========================================");
      System.out.println("GRPC: Sending getBooksByIsbn request for ISBN: " + isbn);

      GetBooksByIsbnResponse response = bookStub.getBooksByIsbn(request);

      System.out.println("GRPC: Response received");
      System.out.println("  Success: " + response.getSuccess());
      System.out.println("  Message: " + response.getMessage());
      System.out.println("  Books count: " + response.getBooksList().size());

      if (response.getSuccess() && !response.getBooksList().isEmpty())
      {
        System.out.println("  ✓ Found " + response.getBooksList().size() + " book(s)");
        for (DTOBook book : response.getBooksList())
        {
          System.out.println("    - Book ID: " + book.getId() + ", Title: " + book.getTitle() + ", State: " + book.getState());
        }
      }
      else
      {
        System.out.println("  ✗ " + response.getMessage());
      }
      System.out.println("========================================");

      return response.getBooksList();
    }
    catch (Exception ex)
    {
      System.err.println("✗ GRPC ERROR in getBooksByIsbn: " + ex.getMessage());
      ex.printStackTrace();
      System.out.println("========================================");
      return new ArrayList<>();
    }
  }

  @Override
  public DTOLoan createLoan(String username, String bookId, int loanDurationDays)
  {
    try
    {
      // Convert bookId from String to int
      int bookIdInt = Integer.parseInt(bookId);

      // Calculate dates based on loanDurationDays
      java.time.LocalDate now = java.time.LocalDate.now();
      java.time.LocalDate dueDate = now.plusDays(loanDurationDays);

      // Use fully qualified name to avoid conflict with shared.CreateLoanRequest
      dk.via.sep3.CreateLoanRequest request = dk.via.sep3.CreateLoanRequest.newBuilder()
              .setUsername(username)
              .setBookId(bookIdInt)
              .setBorrowDate(now.toString())
              .setDueDate(dueDate.toString())
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
    catch (NumberFormatException ex)
    {
      System.err.println("Invalid bookId format: " + bookId);
      ex.printStackTrace();
      return null;
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      return null;
    }
  }
}

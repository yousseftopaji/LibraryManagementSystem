package dk.via.sep3.grpcConnection;
import dk.via.sep3.BookServiceGrpc;
import dk.via.sep3.DTOBook;
import dk.via.sep3.GetAllBooksRequest;
import dk.via.sep3.GetAllBooksResponse;
import dk.via.sep3.model.entities.BookDTO;
import dk.via.sep3.model.entities.LoanDTO;
import dk.via.sep3.model.entities.ReservationDTO;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.List;

public class GrpcConnection implements GrpcConnectionInterface
{
  //I want to check my connection to this server
  private ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",
      9090).usePlaintext().build();

  private BookServiceGrpc.BookServiceBlockingStub stub = BookServiceGrpc.newBlockingStub(
      channel);
//I want to get books by isbn number
    public BookDTO getBookByIsbn(String isbn) {
        GetAllBooksRequest request = GetAllBooksRequest.newBuilder().build();
        GetAllBooksResponse response = stub.getAllBooks(request);
        for (DTOBook dtoBook : response.getBooksList()) {
            if (dtoBook.getIsbn().equals(isbn)) {
                BookDTO bookDTO = new BookDTO();
                bookDTO.setIsbn(dtoBook.getIsbn());
                bookDTO.setTitle(dtoBook.getTitle());
                bookDTO.setAuthor(dtoBook.getAuthor());
                bookDTO.setState(dtoBook.getState());
                return bookDTO;
            }
        }
    }

    @Override
    public LoanDTO createLoan(String username, String isbn) {
       GetAllBooksRequest request = GetAllBooksRequest.newBuilder().build();
         GetAllBooksResponse response = stub.getAllBooks(request);
            for (DTOBook dtoBook : response.getBooksList()) {
                if (dtoBook.getIsbn().equals(isbn)) {
                    LoanDTO loanDTO = new LoanDTO();
                    loanDTO.setIsbn(dtoBook.getIsbn());
                    loanDTO.setUsername(username);
                    return loanDTO;
                }
            }
    }

    @Override
    public ReservationDTO reserveBook(String username, String isbn) {
        GetAllBooksRequest request = GetAllBooksRequest.newBuilder().build();
        GetAllBooksResponse response = stub.getAllBooks(request);
        for (DTOBook dtoBook : response.getBooksList()) {
            if (dtoBook.getIsbn().equals(isbn)) {
                ReservationDTO reservationDTO = new ReservationDTO();
                reservationDTO.setIsbn(dtoBook.getIsbn());
                reservationDTO.setUsername(username);
                return reservationDTO;
            }
        }
    }

    public List<DTOBook> getAllBooks()
  {
    try
    {
      GetAllBooksRequest request = GetAllBooksRequest.newBuilder().build();
      System.out.println("Sending gRPC request to get all books...");
      GetAllBooksResponse response = stub.getAllBooks(request);
      System.out.println(response.getBooksList() + " <- Received gRPC response with all books.");
      return response.getBooksList();
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      return new ArrayList<>();
    }
  }
}

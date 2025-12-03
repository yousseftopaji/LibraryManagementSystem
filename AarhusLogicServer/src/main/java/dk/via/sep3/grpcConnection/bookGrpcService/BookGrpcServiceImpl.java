// AarhusLogicServer/src/main/java/dk/via/sep3/grpcConnection/BookGrpcService.java
package dk.via.sep3.grpcConnection.bookGrpcService;

import dk.via.sep3.*;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookGrpcServiceImpl implements BookGrpcService
{
  private static final Logger logger = LoggerFactory.getLogger(
      BookGrpcService.class);
  private final BookServiceGrpc.BookServiceBlockingStub bookStub;

  public BookGrpcServiceImpl(ManagedChannel channel)
  {
    this.bookStub = BookServiceGrpc.newBlockingStub(channel);
  }

  @Override public List<DTOBook> getAllBooks()
  {
    try
    {
      GetAllBooksRequest request = GetAllBooksRequest.newBuilder().build();
      logger.info("Sending gRPC request to get all books...");
      GetAllBooksResponse response = bookStub.getAllBooks(request);
      logger.info("Received gRPC response with all books.");
      return response.getBooksList();
    }
    catch (Exception ex)
    {
      logger.error("Error fetching all books", ex);
      return new ArrayList<>();
    }
  }

  @Override public List<DTOBook> getBooksByIsbn(String isbn)
  {
    try
    {
      GetBooksByIsbnRequest request = GetBooksByIsbnRequest.newBuilder()
          .setIsbn(isbn).build();
      logger.info("Sending gRPC request to get book by ISBN: {}", isbn);
      GetBooksByIsbnResponse response = bookStub.getBooksByIsbn(request);
      logger.info("Received gRPC response");
      return response.getBooksList();
    }
    catch (Exception ex)
    {
      logger.error(ex.getMessage());
      return new ArrayList<>();
    }
  }

  @Override public DTOBook getBookById(String bookId)
  {
    try
    {
      int bookIdInt = Integer.parseInt(bookId);
      GetBookByIdRequest request = GetBookByIdRequest.newBuilder()
          .setId(bookIdInt).build();
      logger.info("Sending gRPC request to get book by ID: {}", bookId);
      GetBookByIdResponse response = bookStub.getBookById(request);
      logger.info("Received gRPC response");
      return response.getBook();
    }
    catch (NumberFormatException ex)
    {
      logger.error("Invalid bookId format: {}", bookId, ex);
      return null;
    }
    catch (Exception ex)
    {
      logger.error("Error fetching book by ID: {}", bookId, ex);
      return null;
    }
  }

  @Override public DTOBook updateBookStatus(String bookId, String status)
  {
    try
    {
      int bookIdInt = Integer.parseInt(bookId);
      UpdateBookStateRequest request = UpdateBookStateRequest.newBuilder()
          .setId(bookIdInt).setState(status).build();
      logger.info("Sending gRPC request to update book status. ID: {}, Status: {}",
          bookId, status);

      UpdateBookStateResponse response = bookStub
          .updateBookState(request);
      logger.info("Received gRPC response");
      return response.getBook();
    }
    catch (NumberFormatException ex)
    {
      logger.error("Invalid bookId format: {}", bookId, ex);
      return null;
    }
    catch (Exception ex)
    {
      logger.error("Error updating book status. ID: {}, Status: {}", bookId,
          status, ex);
      return null;
    }
  }
}

// AarhusLogicServer/src/main/java/dk/via/sep3/grpcConnection/BookGrpcService.java
package dk.via.sep3.grpcConnection.bookGrpcService;

import dk.via.sep3.*;
import dk.via.sep3.exceptionHandler.GrpcCommunicationException;
import dk.via.sep3.model.domain.Book;
import dk.via.sep3.mapper.bookMapper.BookMapper;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service public class BookGrpcServiceImpl implements BookGrpcService
{
  private static final Logger logger = LoggerFactory.getLogger(
      BookGrpcServiceImpl.class);
  private final BookServiceGrpc.BookServiceBlockingStub bookStub;
  private final BookMapper bookMapper;

  public BookGrpcServiceImpl(ManagedChannel channel, BookMapper bookMapper)
  {
    this.bookStub = BookServiceGrpc.newBlockingStub(channel);
    this.bookMapper = bookMapper;
  }

  @Override public List<Book> getAllBooks()
  {
    try
    {
      GetAllBooksRequest request = GetAllBooksRequest.newBuilder().build();
      logger.info("Sending gRPC request to get all books...");
      GetAllBooksResponse response = bookStub.getAllBooks(request);
      logger.info("Received gRPC response with all books.");
      return response.getBooksList().stream().map(bookMapper::toDomain)
          .toList();
    }
    catch (StatusRuntimeException ex)
    {
      logger.error("gRPC error fetching all books", ex);
      throw new GrpcCommunicationException("Failed to fetch all books", ex);
    }
    catch (Exception ex)
    {
      logger.error("Unexpected error fetching all books", ex);
      throw new GrpcCommunicationException(
          "Unexpected error fetching all books", ex);
    }
  }

  @Override public List<Book> getBooksByIsbn(String isbn)
  {
    try
    {
      // Use the dedicated GetBooksByIsbn RPC and pass the isbn in the request
      GetBooksByIsbnRequest request = GetBooksByIsbnRequest.newBuilder()
          .setIsbn(isbn)
          .build();
      logger.info("Sending gRPC request to get books with ISBN: {}", isbn);
      GetBooksByIsbnResponse response = bookStub.getBooksByIsbn(request);
      logger.info("Received gRPC response with books for ISBN: {}", isbn);
      return response.getBooksList().stream().map(bookMapper::toDomain)
          .toList();
    }
    catch (StatusRuntimeException ex)
    {
      logger.error("gRPC error fetching books by ISBN: {}", isbn, ex);
      throw new GrpcCommunicationException("Failed to fetch books by ISBN", ex);
    }
    catch (Exception ex)
    {
      logger.error("Unexpected error fetching books by ISBN: {}", isbn, ex);
      throw new GrpcCommunicationException(
          "Unexpected error fetching books by ISBN", ex);
    }
  }

  @Override public Book getBookById(int bookId)
  {
    try
    {
      GetBookByIdRequest request = GetBookByIdRequest.newBuilder()
          .setId(bookId).build();
      logger.info("Sending gRPC request to get book by ID: {}", bookId);
      GetBookByIdResponse response = bookStub.getBookById(request);
      logger.info("Received gRPC response GetBookById for ID: {}", bookId);
      return bookMapper.toDomain(response.getBook());
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

  @Override public void updateBookStatus(int bookId, String status)
  {
    try
    {
      UpdateBookStateRequest request = UpdateBookStateRequest.newBuilder()
          .setId(bookId).setState(status).build();
      logger.info(
          "Sending gRPC request to update book status. ID: {}, Status: {}",
          bookId, status);

      UpdateBookStateResponse response = bookStub.updateBookState(request);
      logger.info("Received gRPC response");
      bookMapper.toDomain(response.getBook());

    }
    catch (NumberFormatException ex)
    {
      logger.error("Invalid bookId format: {}", bookId, ex);
    }
    catch (Exception ex)
    {
      logger.error("Error updating book status. ID: {}, Status: {}", bookId,
          status, ex);
    }
  }
}

package dk.via.sep3.model.books;

import dk.via.sep3.exceptionHandler.ResourceNotFoundException;
import dk.via.sep3.grpcConnection.bookGrpcService.BookGrpcService;
import dk.via.sep3.model.domain.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookServiceImpl implements BookService
{
  private static final Logger logger = LoggerFactory.getLogger(
      BookServiceImpl.class);
  private final BookGrpcService bookGrpcService;

  public BookServiceImpl(BookGrpcService bookGrpcService)
  {
    this.bookGrpcService = bookGrpcService;
  }

  /**
   * Retrieve a list of unique books (one per ISBN).
   *
   * This method requests all book copies from the gRPC service and returns
   * a de-duplicated list where each ISBN appears only once.
   *
   * @return list of representative Book objects (one per ISBN)
   */
  @Override public List<Book> getAllBooks()
  {
    logger.info("getAllBooks called");
    List<Book> allBooks = bookGrpcService.getAllBooks();
    logger.info("Retrieved {} books from gRPC service", allBooks.size());
    return createUniqueBooks(allBooks);
  }

  /**
   * Retrieve a representative book by ISBN.
   *
   * If multiple copies exist the method will prefer an available copy; if
   * none are available it returns the first copy. Throws ResourceNotFoundException
   * when no book with the supplied ISBN exists.
   *
   * @param isbn the ISBN to search for
   * @return a representative Book for the ISBN
   * @throws ResourceNotFoundException when no book with the ISBN exists
   */
  @Override public Book getBookByIsbn(String isbn)
  {
    logger.info("getBookByIsbn called");
    List<Book> books = bookGrpcService.getBooksByIsbn(isbn);
    logger.info("Retrieved {} book from gRPC service, size: ", books.size());
    Book book = findRepresentativeBook(books);
    if (book != null)
    {
      logger.info("Representative book found: {}", book);
      return findRepresentativeBook(books);
    }
    else
    {
      logger.info("No representative book found for ISBN: {}", isbn);
      throw new ResourceNotFoundException(
          "Book with ISBN " + isbn + " not found");
    }
  }

  private List<Book> createUniqueBooks(List<Book> allBooks)
  {
    Map<String, Book> uniqueBooksByIsbn = new LinkedHashMap<>();
    if (allBooks == null || allBooks.isEmpty())
    {
      return new ArrayList<>();
    }
    for (Book book : allBooks)
    {
      String isbn = book.getIsbn();
      if (!uniqueBooksByIsbn.containsKey(isbn))
      {
        uniqueBooksByIsbn.put(isbn, book);
      }
    }
    return new ArrayList<>(uniqueBooksByIsbn.values());
  }

  private Book findRepresentativeBook(List<Book> books)
  {
    if (books == null || books.isEmpty())
    {
      return null;
    }
    for (Book b : books)
    {
      String state = b.getState().toString();
      if (state != null && state.trim().equalsIgnoreCase("Available"))
      {
        return b;
      }
    }
    return books.get(0);
  }
}
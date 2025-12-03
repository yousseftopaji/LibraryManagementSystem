package dk.via.sep3.model.books;

import dk.via.sep3.grpcConnection.bookGrpcService.BookGrpcService;
import dk.via.sep3.model.domain.Book;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookServiceImpl implements BookService
{
  private final BookGrpcService bookGrpcService;

  public BookServiceImpl(BookGrpcService bookGrpcService)
  {
    this.bookGrpcService = bookGrpcService;
  }

  @Override public List<Book> getAllBooks()
  {
    List<Book> allBooks = bookGrpcService.getAllBooks();
    return createUniqueBooks(allBooks);
  }

  @Override public Book getBookByIsbn(String isbn)
  {
    List<Book> books = bookGrpcService.getBooksByIsbn(isbn);
    return findRepresentativeBook(books);
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
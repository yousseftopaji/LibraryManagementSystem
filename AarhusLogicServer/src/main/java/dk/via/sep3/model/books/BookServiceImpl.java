package dk.via.sep3.model.books;

import dk.via.sep3.DTOBook;
import dk.via.sep3.grpcConnection.bookPersistenceService.BookPersistenceService;
import dk.via.sep3.shared.book.Book;
import dk.via.sep3.shared.book.State;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service public class BookServiceImpl implements BookService
{
  private final BookPersistenceService bookPersistenceService;

  public BookServiceImpl(BookPersistenceService bookPersistenceService)
  {
    this.bookPersistenceService = bookPersistenceService;
  }

  @Override public List<Book> getAllBooks()
  {
    List<DTOBook> allBooks = bookPersistenceService.getAllBooks();
    return createUniqueBooks(allBooks);
  }

  @Override public Book getBookByIsbn(String isbn)
  {
    List<DTOBook> books = bookPersistenceService.getBooksByIsbn(isbn);
    return findRepresentativeBook(books);
  }

  private List<Book> createUniqueBooks(List<DTOBook> allBooks)
  {
    Map<String, Book> uniqueBooksByIsbn = new LinkedHashMap<>();
    for (DTOBook dtoBook : allBooks)
    {
      String isbn = dtoBook.getIsbn();
      if (!uniqueBooksByIsbn.containsKey(isbn))
      {
        State initialState = State.valueOf(dtoBook.getState().toUpperCase());
        Book bookdto = new Book(String.valueOf(dtoBook.getId()),
            dtoBook.getTitle(), dtoBook.getAuthor(), dtoBook.getIsbn(),
            initialState);
        uniqueBooksByIsbn.put(isbn, bookdto);
      }
    }
    return new ArrayList<>(uniqueBooksByIsbn.values());
  }

  private Book findRepresentativeBook(List<DTOBook> books)
  {
    if (books == null || books.isEmpty())
    {
      return null;
    }
    DTOBook dtoBook = null;
    for (DTOBook b : books)
    {
      if (b.getState().trim().equalsIgnoreCase("Available"))
      {
        dtoBook = b;
        break;
      }
    }
    if (dtoBook == null)
    {
      dtoBook = books.get(0);
    }
    State initialState = State.valueOf(dtoBook.getState().toUpperCase());
    return new Book(String.valueOf(dtoBook.getId()), dtoBook.getTitle(),
        dtoBook.getAuthor(), dtoBook.getIsbn(), initialState);
  }
}
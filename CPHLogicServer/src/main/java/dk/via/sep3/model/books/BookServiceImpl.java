package dk.via.sep3.model.books;

import dk.via.sep3.DTOBook;
import dk.via.sep3.grpcConnection.bookGrpcService.BookGrpcService;
import dk.via.sep3.shared.book.BookDTO;
import dk.via.sep3.shared.book.State;
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

  @Override public List<BookDTO> getAllBooks()
  {
    List<DTOBook> allBooks = bookGrpcService.getAllBooks();
    return createUniqueBooks(allBooks);
  }

  @Override public BookDTO getBookByIsbn(String isbn)
  {
    List<DTOBook> books = bookGrpcService.getBooksByIsbn(isbn);
    return findRepresentativeBook(books);
  }

  private List<BookDTO> createUniqueBooks(List<DTOBook> allBooks)
  {
    Map<String, BookDTO> uniqueBooksByIsbn = new LinkedHashMap<>();
    for (DTOBook dtoBook : allBooks)
    {
      String isbn = dtoBook.getIsbn();
      if (!uniqueBooksByIsbn.containsKey(isbn))
      {
        State initialState = State.valueOf(dtoBook.getState().toUpperCase());
        BookDTO bookdto = new BookDTO(String.valueOf(dtoBook.getId()),
            dtoBook.getTitle(), dtoBook.getAuthor(), dtoBook.getIsbn(),
            initialState);
        uniqueBooksByIsbn.put(isbn, bookdto);
      }
    }
    return new ArrayList<>(uniqueBooksByIsbn.values());
  }

  private BookDTO findRepresentativeBook(List<DTOBook> books)
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
    return new BookDTO(String.valueOf(dtoBook.getId()), dtoBook.getTitle(),
        dtoBook.getAuthor(), dtoBook.getIsbn(), initialState);
  }
}
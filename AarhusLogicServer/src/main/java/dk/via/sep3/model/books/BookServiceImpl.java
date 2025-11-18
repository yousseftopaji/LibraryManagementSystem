package dk.via.sep3.model.books;

import dk.via.sep3.DTOBook;
import dk.via.sep3.grpcConnection.bookGrpcService.BookGrpcService;
import dk.via.sep3.shared.BookDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    java.util.Map<String, BookDTO> uniqueBooksByIsbn = new java.util.LinkedHashMap<>();
    java.util.Map<String, Integer> availableCopiesCount = new java.util.HashMap<>();

    // First pass: count available copies for each ISBN
    for (DTOBook dtoBook : allBooks)
    {
      String isbn = dtoBook.getIsbn();
      String state = dtoBook.getState();

      if (state.trim().equalsIgnoreCase("Available"))
      {
        availableCopiesCount.put(isbn,
            availableCopiesCount.getOrDefault(isbn, 0) + 1);
      }
    }

    // Second pass: create unique books with their available copy counts
    for (DTOBook dtoBook : allBooks)
    {
      String isbn = dtoBook.getIsbn();

      // Only add the book if we haven't seen this ISBN before
      if (!uniqueBooksByIsbn.containsKey(isbn))
      {
        int availableCopies = availableCopiesCount.getOrDefault(isbn, 0);
        BookDTO bookdto = new BookDTO(String.valueOf(dtoBook.getId()),
            dtoBook.getTitle(), dtoBook.getAuthor(), dtoBook.getIsbn(),
            dtoBook.getState(), availableCopies);
        uniqueBooksByIsbn.put(isbn, bookdto);
      }
    }
    return new ArrayList<>(uniqueBooksByIsbn.values());
  }

  @Override public BookDTO getBookByIsbn(String isbn)
  {
    List<DTOBook> books = bookGrpcService.getBooksByIsbn(isbn);
    DTOBook dtoBook = null;
    BookDTO bookdto = null;
    int availableCount = 0;

    if (books != null && !books.isEmpty())
    {
      // Count available copies
      for (DTOBook b : books)
      {
        String state = b.getState();
        if (state.trim().equalsIgnoreCase("Available"))
        {
          availableCount++;
          if (dtoBook == null)
          {
            // Use the first available book as the representative
            dtoBook = b;
          }
        }
      }

      // If no available book found, use the first one
      if (dtoBook == null)
      {
        dtoBook = books.get(0);
      }
    }

    if (dtoBook != null)
    {
      bookdto = new BookDTO(String.valueOf(dtoBook.getId()), dtoBook.getTitle(),
          dtoBook.getAuthor(), dtoBook.getIsbn(), dtoBook.getState(),
          availableCount);
    }
    return bookdto;
  }
}

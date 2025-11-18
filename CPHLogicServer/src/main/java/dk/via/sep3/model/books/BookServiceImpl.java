package dk.via.sep3.model.books;

import dk.via.sep3.DTOBook;
import dk.via.sep3.grpcConnection.GrpcConnectionInterface;
import dk.via.sep3.shared.BookDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService
{
  private final GrpcConnectionInterface grpcConnectionInterface;

  public BookServiceImpl(GrpcConnectionInterface grpcConnectionInterface)
  {
    this.grpcConnectionInterface = grpcConnectionInterface;
  }

    @Override
    public List<BookDTO> getAllBooks()
    {
        List<DTOBook> allBooks = grpcConnectionInterface.getAllBooks();
        java.util.Map<String, BookDTO> uniqueBooksByIsbn = new java.util.LinkedHashMap<>();
        java.util.Map<String, Integer> availableCopiesCount = new java.util.HashMap<>();

        // First pass: count available copies for each ISBN
        for (DTOBook dtoBook : allBooks)
        {
            String isbn = dtoBook.getIsbn();
            String state = dtoBook.getState();

            if (state != null && state.trim().equalsIgnoreCase("Available"))
            {
                availableCopiesCount.put(isbn, availableCopiesCount.getOrDefault(isbn, 0) + 1);
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
                BookDTO bookdto = new BookDTO(
                    String.valueOf(dtoBook.getId()),
                    dtoBook.getTitle(),
                    dtoBook.getAuthor(),
                    dtoBook.getIsbn(),
                    dtoBook.getState(),
                    availableCopies
                );
                uniqueBooksByIsbn.put(isbn, bookdto);
            }
        }

        return new java.util.ArrayList<>(uniqueBooksByIsbn.values());
    }

    @Override
    public BookDTO getBookByIsbn(String isbn)
    {
        System.out.println("========================================");
        System.out.println("getBookByIsbn called with ISBN: " + isbn);

        List<DTOBook> books = grpcConnectionInterface.getBooksByIsbn(isbn);
        System.out.println("Received " + (books != null ? books.size() : "null") + " books from gRPC");

        DTOBook dtoBook = null;
        BookDTO bookdto = null;
        int availableCount = 0;

        if (books != null && !books.isEmpty())
        {
            System.out.println("Processing " + books.size() + " books with ISBN: " + isbn);

            // Count available copies
            for (DTOBook b : books)
            {
                String state = b.getState();
                System.out.println("  Book ID: " + b.getId() + ", State: '" + state + "'");

                if (state != null && state.trim().equalsIgnoreCase("Available"))
                {
                    availableCount++;
                    if (dtoBook == null)
                    {
                        // Use the first available book as the representative
                        dtoBook = b;
                        System.out.println("  -> Using this as representative book");
                    }
                }
            }

            // If no available book found, use the first one
            if (dtoBook == null)
            {
                dtoBook = books.get(0);
                System.out.println("  -> No available book, using first one (ID: " + dtoBook.getId() + ")");
            }
        }
        else
        {
            System.out.println("No books found for ISBN: " + isbn);
        }

        if (dtoBook != null)
        {
            bookdto = new BookDTO(
                String.valueOf(dtoBook.getId()),
                dtoBook.getTitle(),
                dtoBook.getAuthor(),
                dtoBook.getIsbn(),
                dtoBook.getState(),
                availableCount
            );
            System.out.println("Returning BookDTO: " + dtoBook.getTitle() + " with " + availableCount + " available copies");
        }
        else
        {
            System.out.println("Returning null - no book found");
        }

        System.out.println("========================================");
        return bookdto;
    }
}

package dk.via.sep3.controller;


import dk.via.sep3.model.books.BookService;
import dk.via.sep3.shared.BookDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BooksController
{
    private final BookService books;

    public BooksController(BookService books)
    {
        this.books = books;
    }

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks()
    {
        List<BookDTO> uniqueBooks = books.getAllBooks();

        if(uniqueBooks.isEmpty())
        {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(uniqueBooks, HttpStatus.OK);
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<BookDTO> getBooksByIsbn(@PathVariable String isbn)
    {
        System.out.println("\n>>> BooksController: Received request for ISBN: " + isbn);

        BookDTO book = books.getBookByIsbn(isbn);

        if (book == null)
        {
            System.out.println(">>> BooksController: Returning 404 - book is null\n");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        System.out.println(">>> BooksController: Returning book: " + book.getTitle());
        System.out.println(">>> BooksController: Available copies: " + book.getNoOfCopies() + "\n");
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    // Alternative endpoint for /books/isbn/{isbn} pattern
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDTO> getBooksByIsbnAlternative(@PathVariable String isbn)
    {
        System.out.println("\n>>> BooksController: Received request via /isbn/ route for ISBN: " + isbn);
        // Delegate to the main method
        return getBooksByIsbn(isbn);
    }

    // Test endpoint to verify gRPC connection
    @GetMapping("/test/connection")
    public ResponseEntity<String> testConnection()
    {
        try
        {
            List<BookDTO> allBooks = books.getAllBooks();
            return new ResponseEntity<>(
                "✓ Connection OK - Found " + allBooks.size() + " unique books",
                HttpStatus.OK
            );
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(
                "✗ Connection FAILED: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

}

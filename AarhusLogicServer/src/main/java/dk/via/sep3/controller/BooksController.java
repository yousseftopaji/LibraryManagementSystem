package dk.via.sep3.controller;

import dk.via.sep3.model.books.BookService;
import dk.via.sep3.shared.book.BookDTO;
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
        BookDTO book = books.getBookByIsbn(isbn);
        if (book == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    // Alternative endpoint for /books/isbn/{isbn} pattern
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookDTO> getBooksByIsbnAlternative(@PathVariable String isbn)
    {
        // Delegate to the main method
        return getBooksByIsbn(isbn);
    }

}

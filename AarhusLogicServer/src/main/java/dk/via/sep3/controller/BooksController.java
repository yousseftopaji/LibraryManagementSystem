package dk.via.sep3.controller;

import dk.via.sep3.model.books.BookService;
import dk.via.sep3.shared.book.Book;
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
    public ResponseEntity<List<Book>> getAllBooks()
    {
        List<Book> uniqueBooks = books.getAllBooks();

        if(uniqueBooks.isEmpty())
        {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(uniqueBooks, HttpStatus.OK);
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<Book> getBooksByIsbn(@PathVariable String isbn)
    {
        Book book = books.getBookByIsbn(isbn);
        if (book == null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(book, HttpStatus.OK);
    }

}

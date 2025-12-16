package dk.via.sep3.controller;

import dk.via.sep3.model.books.BookService;
import dk.via.sep3.model.domain.Book;
import dk.via.sep3.DTOs.book.BookDTO;
import dk.via.sep3.mapper.bookMapper.BookMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for book operations.
 *
 * <p>Exposes endpoints to search, retrieve and list books. This controller delegates
 * business behavior to {@link dk.via.sep3.model.books.BookService} and performs mapping
 * to/from {@link dk.via.sep3.DTOs.book.BookDTO} via {@link dk.via.sep3.mapper.bookMapper.BookMapper}.
 *
 * <p>Thread-safety: stateless controller; safe for concurrent requests.
 */
@RestController
@RequestMapping("/books")
public class BooksController
{
    private final BookService books;
    private final BookMapper bookMapper;

    public BooksController(BookService books, BookMapper bookMapper)
    {
        this.books = books;
        this.bookMapper = bookMapper;
    }

    /**
     * Return a list of all unique books available in the system.
     *
     * @return list of {@link BookDTO}; never null (empty list if no books)
     */
    @PreAuthorize("hasRole('Reader')")
    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks()
    {
        List<Book> uniqueBooks = books.getAllBooks();
        List<BookDTO> bookDTOs = new ArrayList<>();
        for(Book book : uniqueBooks)
        {
            bookDTOs.add(bookMapper.toDto(book));
        }
        return new ResponseEntity<>(bookDTOs, HttpStatus.OK);
    }

    /**
     * Retrieve a representative book for the specified ISBN.
     *
     * @param isbn the ISBN string to look up; must not be null
     * @return {@link BookDTO} for the representative book
     * @throws dk.via.sep3.exceptionHandler.ResourceNotFoundException if no book is found
     */
    @PreAuthorize("hasRole('Reader')")
    @GetMapping("/{isbn}")
    public ResponseEntity<BookDTO> getBooksByIsbn(
            @PathVariable String isbn)
    {
        Book book = books.getBookByIsbn(isbn);
        BookDTO bookDTO = bookMapper.toDto(book);
        return new ResponseEntity<>(bookDTO, HttpStatus.OK);
    }
}
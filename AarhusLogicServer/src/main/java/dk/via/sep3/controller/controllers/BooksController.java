package dk.via.sep3.controller.controllers;

import dk.via.sep3.model.books.BookService;
import dk.via.sep3.model.domain.Book;
import dk.via.sep3.shared.book.BookDTO;
import dk.via.sep3.mapper.bookMapper.BookMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController @RequestMapping("/books") public class BooksController
{
  private final BookService books;
  private final BookMapper bookMapper;

  public BooksController(BookService books, BookMapper bookMapper)
  {
    this.books = books;
    this.bookMapper = bookMapper;
  }

  @GetMapping public ResponseEntity<List<BookDTO>> getAllBooks()
  {
    List<Book> uniqueBooks = books.getAllBooks();
    List<BookDTO> bookDTOs = new ArrayList<>();
    for(Book book : uniqueBooks)
    {
      bookDTOs.add(bookMapper.toDto(book));
    }
    return new ResponseEntity<>(bookDTOs, HttpStatus.OK);
  }

  @GetMapping("/{isbn}") public ResponseEntity<BookDTO> getBooksByIsbn(
      @PathVariable String isbn)
  {
    Book book = books.getBookByIsbn(isbn);
    BookDTO bookDTO = bookMapper.toDto(book);
    return new ResponseEntity<>(bookDTO, HttpStatus.OK);
  }
}

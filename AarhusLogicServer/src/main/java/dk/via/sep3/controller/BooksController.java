package dk.via.sep3.controller;


import dk.via.sep3.DTOBook;
import dk.via.sep3.model.BookList;
import dk.via.sep3.model.entities.BookDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BooksController
{
  private final BookList bookList;

  public BooksController(BookList bookList)
  {
    this.bookList = bookList;
  }

  @GetMapping
  public ResponseEntity<List<BookDTO>> getAllBooks()
  {
    List<BookDTO> books = bookList.getAllBooks().stream()
        .map(grpcBook -> new BookDTO(
            grpcBook.getId(),
            grpcBook.getTitle(),
            grpcBook.getAuthor(),
            grpcBook.getIsbn(),
            grpcBook.getState()
        ))
        .collect(Collectors.toList());
    
    return new ResponseEntity<>(books, HttpStatus.OK);
  }

  @GetMapping("/{isbn}")
  public ResponseEntity<BookDTO> getBook(@PathVariable String isbn)
  {
    try
    {
      DTOBook grpcBook = bookList.getBook(isbn);
      if (grpcBook == null)
      {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
      BookDTO bookDTO = new BookDTO(
          grpcBook.getId(),
          grpcBook.getTitle(),
          grpcBook.getAuthor(),
          grpcBook.getIsbn(),
          grpcBook.getState()
      );
      return new ResponseEntity<>(bookDTO, HttpStatus.OK);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}

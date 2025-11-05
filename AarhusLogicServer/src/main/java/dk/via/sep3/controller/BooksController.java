package dk.via.sep3.controller;


import dk.via.sep3.model.BookList;
import dk.via.sep3.model.entities.BookDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class BooksController
{
  private final BookList bookList;

  public BooksController(BookList bookList)
  {
    this.bookList = bookList;
  }

  @GetMapping("/Books")
  public ResponseEntity<List<BookDTO>> getAllBooks()
  {
    List<BookDTO> books = bookList.getAllBooks().stream()
        .map(grpcBook -> new BookDTO(
            grpcBook.getTitle(),
            grpcBook.getAuthor(),
            grpcBook.getIsbn(),
            grpcBook.getState()
        ))
        .collect(Collectors.toList());
    
    return new ResponseEntity<>(books, HttpStatus.OK);
  }
}

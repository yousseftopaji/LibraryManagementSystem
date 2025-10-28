package dk.via.sep3.controller;

import dk.via.sep3.DTOBook;
import dk.via.sep3.model.BookList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BooksController
{
  private final BookList bookList;

  public BooksController(BookList bookList)
  {
    this.bookList = bookList;
  }

  @GetMapping("/Books")
  public synchronized ResponseEntity<List<DTOBook>> getAllPlanets()
  {
    List<DTOBook> dtoBooks = new ArrayList<>(bookList.getAllBooks());
    return new ResponseEntity<>( dtoBooks, HttpStatus.OK );
  }
}

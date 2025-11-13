package dk.via.sep3.model;

import dk.via.sep3.DTOBook;
import dk.via.sep3.model.entities.Book;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BookList
{
  List<DTOBook> getAllBooks();
    List<DTOBook> getBookByIsbn(String isbn);
}

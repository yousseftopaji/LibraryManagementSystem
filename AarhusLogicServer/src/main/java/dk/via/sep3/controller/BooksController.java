package dk.via.sep3.controller;


import dk.via.sep3.model.BookList;
import dk.via.sep3.model.entities.BookDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
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

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<List<BookDTO>> getBooksByIsbn(@PathVariable String isbn)
    {
        List<BookDTO> books = bookList.getAllBooks().stream()
                .filter(grpcBook -> grpcBook.getIsbn().equals(isbn))
                .map(grpcBook -> new BookDTO(
                        grpcBook.getId(),
                        grpcBook.getTitle(),
                        grpcBook.getAuthor(),
                        grpcBook.getIsbn(),
                        grpcBook.getState()
                ))
                .collect(Collectors.toList());

        if (books.isEmpty())
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(books, HttpStatus.OK);
    }

}

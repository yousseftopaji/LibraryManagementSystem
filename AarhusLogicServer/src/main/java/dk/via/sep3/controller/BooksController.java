package dk.via.sep3.controller;


import dk.via.sep3.model.BookList;
import dk.via.sep3.model.entities.BookDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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
        // Group books by ISBN and return one book per ISBN with available copies count
        List<dk.via.sep3.DTOBook> allBooks = bookList.getAllBooks();

        Map<String, List<dk.via.sep3.DTOBook>> booksByIsbn = allBooks.stream()
                .collect(Collectors.groupingBy(dk.via.sep3.DTOBook::getIsbn));

        List<BookDTO> uniqueBooks = booksByIsbn.entrySet().stream()
                .map(entry -> {
                    String isbn = entry.getKey();
                    List<dk.via.sep3.DTOBook> booksWithSameIsbn = entry.getValue();

                    // Get the first book as representative
                    dk.via.sep3.DTOBook representativeBook = booksWithSameIsbn.get(0);

                    // Count available copies
                    int availableCopies = (int) booksWithSameIsbn.stream()
                            .filter(book -> "Available".equalsIgnoreCase(book.getState()))
                            .count();

                    return new BookDTO(
                            representativeBook.getId(),
                            representativeBook.getTitle(),
                            representativeBook.getAuthor(),
                            representativeBook.getIsbn(),
                            representativeBook.getState(),
                            availableCopies
                    );
                })
                .sorted((b1, b2) -> b1.getTitle().compareTo(b2.getTitle()))
                .collect(Collectors.toList());

        return new ResponseEntity<>(uniqueBooks, HttpStatus.OK);
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<List<BookDTO>> getBooksByIsbn(@PathVariable String isbn)
    {
        System.out.println("Received request for books with ISBN: " + isbn);
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

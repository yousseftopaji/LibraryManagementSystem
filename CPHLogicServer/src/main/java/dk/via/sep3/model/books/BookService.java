package dk.via.sep3.model.books;

import dk.via.sep3.shared.BookDTO;

import java.util.List;


public interface BookService {
    List<BookDTO> getAllBooks();
    BookDTO getBookByIsbn(String isbn);
}

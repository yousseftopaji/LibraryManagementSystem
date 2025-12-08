package dk.via.sep3.model.books;

import dk.via.sep3.model.domain.Book;

import java.util.List;


public interface BookService {
    List<Book> getAllBooks();
    Book getBookByIsbn(String isbn);
}

package dk.via.sep3.application.services.books;

import dk.via.sep3.application.domain.Book;

import java.util.List;


public interface BookService {
    List<Book> getAllBooks();
    Book getBookByIsbn(String isbn);
}

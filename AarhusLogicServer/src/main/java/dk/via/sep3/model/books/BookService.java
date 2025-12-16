package dk.via.sep3.model.books;

import dk.via.sep3.model.domain.Book;

import java.util.List;


/**
 * Service interface that defines book-related operations.
 *
 * <p>Implementations must enforce business invariants (e.g., uniqueness, state transitions).
 */
public interface BookService {
    /**
     * Retrieve all unique books available in the system. Implementations should ensure
     * duplicates by ISBN are filtered out.
     *
     * @return list of {@link Book}; never null (may be empty)
     */
    List<Book> getAllBooks();

    /**
     * Retrieve a representative book for the given ISBN.
     *
     * @param isbn the ISBN to look up; must not be null
     * @return the representative {@link Book}
     * @throws dk.via.sep3.exceptionHandler.ResourceNotFoundException if no book is found
     */
    Book getBookByIsbn(String isbn);
}

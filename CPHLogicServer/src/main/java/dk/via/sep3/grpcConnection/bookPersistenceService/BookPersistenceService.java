package dk.via.sep3.grpcConnection.bookPersistenceService;

// AarhusLogicServer/src/main/java/dk/via/sep3/grpcConnection/BookGrpcServiceInterface.java

import dk.via.sep3.DTOBook;

import java.util.List;

public interface BookPersistenceService
{
    List<DTOBook> getAllBooks();
    List<DTOBook> getBooksByIsbn(String isbn);
    DTOBook getBookById(String bookId);
    void updateBookStatus(String bookId, String status);
}
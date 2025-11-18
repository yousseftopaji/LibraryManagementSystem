package dk.via.sep3.grpcConnection.bookGrpcService;

// AarhusLogicServer/src/main/java/dk/via/sep3/grpcConnection/BookGrpcServiceInterface.java

import dk.via.sep3.DTOBook;

import java.util.List;

public interface BookGrpcService
{
  List<DTOBook> getAllBooks();
  List<DTOBook> getBooksByIsbn(String isbn);
  DTOBook getBookById(String bookId);
  DTOBook updateBookStatus(String bookId, String status);
}
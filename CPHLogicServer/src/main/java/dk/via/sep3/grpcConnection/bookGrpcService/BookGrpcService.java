package dk.via.sep3.grpcConnection.bookGrpcService;

// AarhusLogicServer/src/main/java/dk/via/sep3/grpcConnection/BookGrpcServiceInterface.java

import dk.via.sep3.model.domain.Book;

import java.util.List;

public interface BookGrpcService
{
  List<Book> getAllBooks();
  List<Book> getBooksByIsbn(String isbn);
  Book getBookById(int bookId);
  void updateBookStatus(int bookId, String status);
}